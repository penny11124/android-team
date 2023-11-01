package com.logos.ticket_module;

import java.security.PublicKey;

import org.fidoalliance.fdo.certutils.PemLoader;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

// import java.security.PrivateKey;
// import java.security.PublicKey;
// import java.security.interfaces.ECKey;
// import java.security.interfaces.RSAKey;

public class UTicket extends Ticket {
    private TicketType ticketType;
    private PublicKey holderPublicKey;
    private Command command;
    private byte[] validCondition;
    private byte[] data;
    private byte[] info;

    public UTicket(int sessionNum, String devicePubKey, String holderPublicKey) {
        super(sessionNum, devicePubKey);
        this.holderPublicKey = PemLoader.loadPublicKeys(holderPublicKey).get(0);
    }

    public UTicket(int sessionNum, PublicKey devicePubKey, PublicKey holderPublicKey) {
        super(sessionNum, devicePubKey);
        this.holderPublicKey = holderPublicKey;
    }

    public UTicket(int sessionNum, String devicePubKey, PublicKey holderPublicKey) {
        super(sessionNum, devicePubKey);
        this.holderPublicKey = holderPublicKey;
    }

    public UTicket(Composite cose) {
        super(cose);

        this.holderPublicKey = cryptoService
                .decode(this.payload.getAsComposite(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY));
        this.ticketType = TicketType.valueOf(new String(this.payload.getAsBytes(Constants.CBOR_U_TICKET_TICKET_TYPE)));
        if (this.ticketType == TicketType.ACCESS) {

            this.command = new Command(this.payload.getAsBytes(Constants.CBOR_U_TICKET_COMMAND));
        }

        this.validCondition = this.payload.getAsBytes(Constants.CBOR_U_TICKET_VALID_CONDITION);
    }

    @Override
    public Composite toCoseComposite() {
        final byte[] protectedHeader = Composite.newMap()
                .set(Const.COSE_ALG, this.getCoseSignatureAlg())
                .toBytes();

        Composite ticket = Composite.newMap().set(Constants.CBOR_TICKET_PROTOCOL_VERSION,
                this.getProtocolVersion())
                .set(Constants.CBOR_TICKET_SESSION_NUMBER, this.getSessionNum())
                .set(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY, this.publicToComposite(this.getDevicePubKey()))
                .set(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY, this.publicToComposite(this.getHolderPublicKey()))
                .set(Constants.CBOR_U_TICKET_TICKET_TYPE, this.getTicketType().name().getBytes());
        if (this.command != null) {
            ticket.set(Constants.CBOR_U_TICKET_COMMAND, this.getCommand().getByte());
        }
        if (this.validCondition != null) {
            ticket.set(Constants.CBOR_U_TICKET_VALID_CONDITION, this.getValidCondition());
        }
        final byte[] payload = ticket.toBytes();

        return Composite.newArray()
                .set(Const.COSE_SIGN1_PROTECTED, protectedHeader)
                .set(Const.COSE_SIGN1_UNPROTECTED, Composite.newMap())
                .set(Const.COSE_SIGN1_PAYLOAD, payload)
                .set(Const.COSE_SIGN1_SIGNATURE, this.getSignature());
    }

    @Override
    public Composite toComposite() {
        Composite ticket = Composite.newMap().set(Constants.CBOR_TICKET_PROTOCOL_VERSION,
                this.getProtocolVersion())
                .set(Constants.CBOR_TICKET_SESSION_NUMBER, this.getSessionNum())
                .set(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY, this.publicToComposite(this.getDevicePubKey()))
                .set(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY, this.publicToComposite(this.getHolderPublicKey()))
                .set(Constants.CBOR_U_TICKET_TICKET_TYPE, this.getTicketType().name().getBytes());
        if (this.command != null) {
            ticket.set(Constants.CBOR_U_TICKET_COMMAND, this.getCommand().getByte());
        }
        if (this.validCondition != null) {
            ticket.set(Constants.CBOR_U_TICKET_VALID_CONDITION, this.getValidCondition());
        }

        return ticket;
    }

    @Override
    public byte[] toBytes() {
        return this.toComposite().toBytes();
    }

    public PublicKey getHolderPublicKey() {
        return this.holderPublicKey;
    }

    public Command getCommand() {
        return command;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public byte[] getValidCondition() {
        return validCondition;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public void setValidCondition(byte[] validCondition) {
        this.validCondition = validCondition;
    }
}
