package com.logos.ticket_module.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import com.logos.device.DeviceCommand;
import com.logos.ticket_module.Command;
import com.logos.ticket_module.Constants;
import com.logos.ticket_module.Execution;
import com.logos.ticket_module.RTicket;
import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.TicketType;
import com.logos.ticket_module.UTicket;
import com.logos.ticket_module.storage.DeviceStorageInterface;

import org.fidoalliance.fdo.protocol.CloseableKey;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;
import org.fidoalliance.fdo.protocol.CryptoService;
import org.fidoalliance.fdo.protocol.DispatchException;
import org.fidoalliance.fdo.protocol.KeyExchangeResult;

public class UDeviceService extends UService {
    private DeviceStorageInterface storage;
    private CryptoService cryptoService;
    private Execution execution;
    private Command command;
    private Command[] commands;

    protected byte[] kexA;
    protected byte[] nonceProveHolder;
    protected PublicKey holderPublicKey;
    protected Composite state;
    protected UTicket ticket;
    protected Composite ticketCose;
    protected Composite ticketPayload;

    // ticket
    protected TicketType ticketType;
    protected byte[] commandByte;

    public UDeviceService(DeviceStorageInterface storage, Execution execution) {
        this.execution = execution;
        this.storage = storage;
        this.cryptoService = new CryptoService();
    }

    private void doApplyTicket(Composite request, Composite reply) {
        // storage.starting(request, reply);
        this.ticketCose = request.getAsComposite(Const.SM_BODY);
        this.ticket = new UTicket(ticketCose);

        this.ticketPayload = Composite.fromObject(ticketCose.getAsBytes(Const.COSE_SIGN1_PAYLOAD));
        // store ticket for r ticket
        // this.ticket = new UTicket(ticketCose);
        this.ticketType = TicketType
                .valueOf(new String(this.ticketPayload.getAsBytes(Constants.CBOR_U_TICKET_TICKET_TYPE)));
        this.storage.storeTicket(ticketCose);
        // store and check vc and command
        if (this.ticketType != TicketType.INIT) {
            // verify ticket if has owner
            cryptoService.verify(storage.getOwnerPublicKey(), ticketCose, null, null, null);
        }

        // this.holderPublicKey = this.ticket.getHolderPublicKey();
        this.holderPublicKey = cryptoService
                .decode(this.ticketPayload.getAsComposite(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY));
        

        if (!ticket.getDevicePubKey().equals(storage.getPublicKey())) {
            System.out.println("device public key not match");
            return;
        }

        if (this.ticket.getSessionNum() != storage.getSessionNum()+1) {
            System.out.println("session number not match");
            System.out.println("ticket session number: " + this.ticket.getSessionNum());
            System.out.println("storage session number: " + storage.getSessionNum());
            return;
        }

        this.nonceProveHolder = cryptoService.getRandomBytes(Const.NONCE16_SIZE);

        Composite replyPayload = Composite.newArray().set(Constants.PROVE_HOLDER_NONCE, this.nonceProveHolder);

        reply.set(Const.SM_MSG_ID, Constants.HELLO_DEVICE);
        reply.set(Const.SM_BODY, replyPayload);
        // storage.started(request, reply);
    }

    private void doProveHolder(Composite request, Composite reply) {
        // storage.starting(request, reply);

        Composite cose = request.getAsComposite(Const.SM_BODY);
        Composite payload = Composite.fromObject(cose.getAsBytes(Const.COSE_SIGN1_PAYLOAD));

        byte[] receivedNonceProveHolder = payload.getAsBytes(Constants.PROVE_HOLDER_NONCE);
        byte[] nonceProveDevice = payload.getAsBytes(Constants.PROVE_DEVICE_NONCE);

        // verify nonce from hello
        cryptoService.verifyBytes(receivedNonceProveHolder, this.nonceProveHolder);

        // verify this message
        cryptoService.verify(this.holderPublicKey, cose, null, null, null);

        Composite replyPayload;
        if (needKeyExchange()) {
            this.kexA = payload.getAsBytes(Constants.PROVE_HOLDER_KEY_EXCHANGE_A);
            // response challenge from holder to prove device itself
            Composite deviceState = cryptoService.getKeyExchangeMessage(storage.getKexSuiteName(), Const.KEY_EXCHANGE_B,
                    this.holderPublicKey);

            KeyExchangeResult kxResult = cryptoService.getSharedSecret(this.kexA, deviceState, null);

            this.state = cryptoService.getEncryptionState(kxResult, storage.getCipherSuiteName());
            // build EAT token based on private key and sign
            /**
             * original code, build EAT(cwt). ueid: EAT's uuid. deviceState: kexB.
             *
             * Composite iotPayload = Composite.newArray().set(Const.FIRST_KEY,
             * deviceState.getAsBytes(Const.FIRST_KEY));
             *
             * Composite payload = Composite.newMap().set(Const.EAT_NONCE,
             * nonceTo2ProveDv).set(Const.EAT_UEID, ueid) .set(Const.EAT_FDO, iotPayload);
             */
            // set next nonce
            this.nonceProveHolder = cryptoService.getRandomBytes(Const.NONCE16_SIZE);

            replyPayload = Composite.newArray()
                    .set(Constants.PROVE_DEVICE_NONCE_FIRST, nonceProveDevice)
                    .set(Constants.PROVE_HOLDER_NONCE_SECOND, this.nonceProveHolder)
                    .set(Constants.PROVE_HOLDER_KEY_EXCHANGE_B, deviceState.getAsBytes(Const.FIRST_KEY));
        } else {
            // execute command
            this.nonceProveHolder = cryptoService.getRandomBytes(Const.NONCE16_SIZE);

            replyPayload = Composite.newArray()
                    .set(Constants.PROVE_DEVICE_NONCE_FIRST, nonceProveDevice);

            if (this.ticketPayload.getAsBytes(Constants.CBOR_U_TICKET_COMMAND) != null) {

                byte[] command = this.ticketPayload.getAsBytes(Constants.CBOR_U_TICKET_COMMAND);
                // check vc
                // check command
                if (command != null && command.length > 0) {
                this.command = new DeviceCommand(command);
                execution.executeCommand(this.command);
                }
            }

            if (this.ticketType == TicketType.INIT) {
                this.storage.setOwnerPublicKey(this.holderPublicKey);
            }
            if (this.ticketType == TicketType.TRANSFER_OWNERSHIP) {
                this.storage.setOwnerPublicKey(this.holderPublicKey);
            }
            if (this.ticketType == TicketType.REVOCATION) {

            }
        }

        Composite signedPayload = null;
        try {
            CloseableKey key = new CloseableKey(storage.getDevicePrivateKey());
            signedPayload = cryptoService.sign(key.get(), replyPayload.toBytes(),
                    cryptoService.getCoseAlgorithm(key.get()));
        } catch (Exception e) {
            throw new DispatchException(e);
        }

        reply.set(Const.SM_MSG_ID, Constants.PROVE_DEVICE);
        reply.set(Const.SM_BODY, signedPayload);

        // storage.started(request, reply);
    }

    private void doSessionPackage(Composite request, Composite reply) {
        // storage.continuing(request, reply);

        Composite body = request.getAsComposite(Const.SM_BODY);
        Composite message = Composite.fromObject(cryptoService.decrypt(body, this.state));

        byte[] signedBytes = message.getAsBytes(Const.COSE_SIGN1_PAYLOAD);
        Composite signedBody = Composite.fromObject(signedBytes);
        cryptoService.verify(this.holderPublicKey, message, null, null, null);
        message = signedBody;

        byte[] receivedNonce = message.getAsBytes(Constants.CBOR_SESSION_PROVE_HOLDER_NONCE);
        cryptoService.verifyBytes(receivedNonce, this.nonceProveHolder);

        this.nonceProveHolder = cryptoService.getRandomBytes(Const.NONCE16_SIZE);

        byte[] command = message.getAsBytes(Constants.CBOR_SESSION_COMMAND);
        // check vc

        // check if this session package has command or just establish session.
        // check command
        if (command != null && command.length > 0) {
            this.command = new DeviceCommand(command);
            execution.executeCommand(this.command);
        }
        Composite payload = Composite.newArray();

        payload.set(Constants.CBOR_SESSION_PROVE_HOLDER_NONCE, nonceProveHolder);

        CloseableKey key = new CloseableKey(storage.getPrivateKey());
        Composite cose = cryptoService.sign(key.get(), payload.toBytes(), cryptoService.getCoseAlgorithm(
                this.holderPublicKey));

        body = cryptoService.encrypt(cose.toBytes(), this.state);
        reply.set(Const.SM_MSG_ID, Constants.REPLY_SESSION_PACK);
        reply.set(Const.SM_BODY, body);
        // storage.continued(request, reply);
    }

    private void doDone(Composite request, Composite reply) {
        Composite ticket = this.storage.getUTicket();
        MessageDigest digest;
        byte[] hash = new byte[0];
        try {
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(ticket.toBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RTicket rTicket = new RTicket(storage.getSessionNum(),
                storage.getPublicKey());
        rTicket.setHash1(hash);

        reply.set(Const.SM_MSG_ID, Constants.DONE);

        // reply.set(Const.SM_BODY, rTicket.toCoseComposite());
        reply.set(Const.SM_BODY, Composite.fromObject(Const.EMPTY_BYTE));
    }

    private void doError(Composite request, Composite reply) {
    }

    @Override
    public CryptoService getCryptoService() {
        return this.cryptoService;
    }

    @Override
    public boolean dispatch(Composite request, Composite reply) {
        switch (request.getAsNumber(Const.SM_MSG_ID).intValue()) {
            case Constants.APPLY_TICKET:
                doApplyTicket(request, reply);
                return false;
            case Constants.PROVE_HOLDER:
                doProveHolder(request, reply);
                return false;
            case Constants.SESSION_PACK:
                doSessionPackage(request, reply);
                return false;
            case Constants.PENDING:
                doDone(request, reply);
                return false;
            case Constants.DONE:
                doDone(request, reply);
                return true;
            case Constants.ERROR:
                doError(request, reply);
                return true;
            default:
                throw new RuntimeException(new UnsupportedOperationException());
        }
    }

    @Override
    public Ticket signTicket(Ticket ticket) {
        Composite signedTickeComposite = cryptoService.sign(
                storage.getPrivateKey(),
                ticket.toBytes(),
                cryptoService.getCoseAlgorithm(ticket.getDevicePubKey()));
        return new RTicket(signedTickeComposite);
    }

    @Override
    protected boolean needKeyExchange() {
        if (TicketType.ACCESS_SESSION == this.ticketType) {
            return true;
        }
        return false;
    }
}
