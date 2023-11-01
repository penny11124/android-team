package com.logos.ticket_module.service;

import java.security.PublicKey;

import com.logos.ticket_module.Constants;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.TicketType;
import com.logos.ticket_module.UTicket;
import com.logos.ticket_module.storage.UserStorageInterface;

import org.fidoalliance.fdo.protocol.CloseableKey;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;
import org.fidoalliance.fdo.protocol.CryptoService;
import org.fidoalliance.fdo.protocol.DispatchException;
import org.fidoalliance.fdo.protocol.DispatchResult;
import org.fidoalliance.fdo.protocol.InvalidMessageException;
import org.fidoalliance.fdo.protocol.KeyExchangeResult;

public class UUserService extends UService {
    private UserStorageInterface storage;
    private CryptoService cryptoService;

    protected byte[] kexB;
    protected byte[] nonceProveDevice;
    protected Composite state;
    protected Composite ticketCose;
    protected UTicket ticket;
    protected PublicKey holderPublicKey;
    protected PublicKey devicePublicKey;
    private byte[] nonceProveHolder;

    public UUserService(UserStorageInterface storage) {
        this.storage = storage;
        this.cryptoService = new CryptoService();
    }

    private void doDeviceHello(Composite request, Composite reply) {
        // storage.starting(request, reply);

        try {
            Composite body = request.getAsComposite(Const.SM_BODY);

            this.nonceProveHolder = body.getAsBytes(Constants.DEVICE_HELLO_NONCE);

            // store parameter A
            // storage.setHolderState(this.state);

            // build reply
            this.ticketCose = storage.getUTicket();
            this.ticket = new UTicket(ticketCose);

            this.nonceProveDevice = cryptoService.getRandomBytes(Const.NONCE16_SIZE);

            Composite payload;
            if (needKeyExchange()) {
                // key exchange parameter A
                this.state = cryptoService.getKeyExchangeMessage(Constants.UREKA_KEX_ALG_NAME,
                        Const.KEY_EXCHANGE_A, null);

                payload = Composite.newArray().set(Constants.PROVE_HOLDER_NONCE, nonceProveHolder)
                        .set(Constants.PROVE_DEVICE_NONCE,
                                this.nonceProveDevice)
                        .set(Constants.PROVE_HOLDER_KEY_EXCHANGE_A, this.state.getAsBytes(Const.FIRST_KEY));
            } else {
                payload = Composite.newArray().set(Constants.PROVE_HOLDER_NONCE, nonceProveHolder)
                        .set(Constants.PROVE_DEVICE_NONCE,
                                this.nonceProveDevice);
            }

            // Composite pubEncKey =
            // ticketCose.getAsComposite(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY);
            // this.holderPublicKey = cryptoService.decode(pubEncKey);
            this.holderPublicKey = this.ticket.getHolderPublicKey();
            Composite cose = null;
            try {
                // get private key and sign response
                CloseableKey key = new CloseableKey(storage.getPrivateKey());
                cose = cryptoService.sign(key.get(), payload.toBytes(),
                        cryptoService.getCoseAlgorithm(this.holderPublicKey));
            } catch (Exception e) {
                throw new DispatchException(e);
            }

            reply.set(Const.SM_MSG_ID, Constants.PROVE_HOLDER);
            reply.set(Const.SM_BODY, cose);
            // storage.started(request, reply);

        } catch (Exception e) {
            e.printStackTrace();
            storage.failed(request, reply);
        }
    }

    private void doProveDevice(Composite request, Composite reply) {
        // storage.continuing(request, reply);

        Composite body = request.getAsComposite(Const.SM_BODY);
        this.devicePublicKey = this.ticket.getDevicePubKey();

        if (!cryptoService.verify(this.devicePublicKey, body, null, null, null)) {
            throw new InvalidMessageException();
        }

        Composite payload = Composite.fromObject(body.getAsBytes(Const.COSE_SIGN1_PAYLOAD));

        byte[] receivedNonceProveDevice = payload.getAsBytes(Constants.PROVE_DEVICE_NONCE_FIRST);

        cryptoService.verifyBytes(receivedNonceProveDevice, this.nonceProveDevice);

        int messageId;
        if (needKeyExchange()) {
            this.nonceProveHolder = payload.getAsBytes(Constants.PROVE_HOLDER_NONCE_SECOND);
            byte[] kexB = payload.getAsBytes(Constants.PROVE_HOLDER_KEY_EXCHANGE_B);

            KeyExchangeResult kxResult = cryptoService.getSharedSecret(kexB, this.state,
                    storage.getPrivateKey());

            this.state = cryptoService.getEncryptionState(kxResult, storage.getCipherSuiteName());
            // this.nonceProveDevice = cryptoService.getRandomBytes(Const.NONCE16_SIZE);
            payload = Composite.newArray();

            // payload.set(Constants.CBOR_SESSION_COMMAND,
            // this.ticket.getCommand().getByte());
            // payload.set(Constants.CBOR_SESSION_PROVE_HOLDER_NONCE, nonceProveHolder);

            CloseableKey key = new CloseableKey(storage.getPrivateKey());
            Composite cose = cryptoService.sign(key.get(), payload.toBytes(), cryptoService.getCoseAlgorithm(
                    this.holderPublicKey));
            body = cryptoService.encrypt(cose.toBytes(), this.state);
            messageId = Constants.PENDING;
        } else {
            body = Composite.newArray();
            messageId = Constants.DONE;
        }

        reply.set(Const.SM_MSG_ID, messageId);
        reply.set(Const.SM_BODY, body);
        // storage.continued(request, reply);
    }

    private void doReplySession(Composite request, Composite reply) {
        Composite body = request.getAsComposite(Const.SM_BODY);
        Composite message = Composite.fromObject(cryptoService.decrypt(body, this.state));

        byte[] signedBytes = message.getAsBytes(Const.COSE_SIGN1_PAYLOAD);
        Composite signedBody = Composite.fromObject(signedBytes);
        cryptoService.verify(this.holderPublicKey, message, null, null, null);
        message = signedBody;
        this.nonceProveHolder = message.getAsBytes(Constants.CBOR_SESSION_PROVE_HOLDER_NONCE);
    }

    @Override
    public CryptoService getCryptoService() {
        return this.cryptoService;
    }

    @Override
    public boolean dispatch(Composite request, Composite reply) {
        switch (request.getAsNumber(Const.SM_MSG_ID).intValue()) {
            case Constants.HELLO_DEVICE:
                doDeviceHello(request, reply);
                return false;
            case Constants.PROVE_DEVICE:
                doProveDevice(request, reply);
                return false;
            case Constants.REPLY_SESSION_PACK:
                doReplySession(request, reply);
                return false;
            case Constants.DONE:
                return true;
            case Constants.ERROR:
                return true;
            default:
                System.out.println(request.getAsNumber(Const.SM_MSG_ID).intValue());
                throw new RuntimeException(new UnsupportedOperationException());
        }
    }

    public DispatchResult applyTicket() {
        Composite ticket = storage.getUTicket();

        // storage.starting(Const.EMPTY_MESSAGE, Const.EMPTY_MESSAGE);
        DispatchResult dr = new DispatchResult(
                Composite.newArray().set(Const.SM_LENGTH, Const.DEFAULT).set(Const.SM_MSG_ID, Const.TO2_HELLO_DEVICE)
                        .set(Const.SM_PROTOCOL_VERSION, ticket.getAsString(Constants.CBOR_TICKET_PROTOCOL_VERSION))
                        .set(Const.SM_PROTOCOL_INFO, Composite.fromObject(Const.EMPTY_BYTE)).set(Const.SM_BODY, ticket),
                false);

        // storage.started(Const.EMPTY_MESSAGE, dr.getReply());
        return dr;
    }

    public Ticket signTicket(Ticket ticket) {
        Composite signedTickeComposite = cryptoService.sign(
                storage.getPrivateKey(),
                ticket.toBytes(),
                cryptoService.getCoseAlgorithm(ticket.getDevicePubKey()));
        return new UTicket(signedTickeComposite);
    }

    @Override
    protected boolean needKeyExchange() {
        if (TicketType.ACCESS_SESSION == this.ticket.getTicketType()) {
            return true;
        }
        return false;
    }
}
