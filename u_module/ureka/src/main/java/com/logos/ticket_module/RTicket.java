package com.logos.ticket_module;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

public class RTicket extends Ticket {
    private byte[] hash1;
    // private byte[] hash2;

    public RTicket(int sessionNum, PublicKey devicePubKey) {
        super(sessionNum, devicePubKey);
        // TODO Auto-generated constructor stub
    }

    public RTicket(int sessionNum, String devicePubKey) {
        super(sessionNum, devicePubKey);
        // TODO Auto-generated constructor stub
    }

    public RTicket(Composite cose) {
        super(cose);

        this.hash1 = this.payload.getAsBytes(Constants.CBOR_R_TICKET_HASH);
    }

    @Override
    public Composite toComposite() {
        Composite ticketComposite = Composite.newMap();
        ticketComposite.set(Constants.CBOR_TICKET_PROTOCOL_VERSION, this.getProtocolVersion());
        ticketComposite.set(Constants.CBOR_TICKET_SESSION_NUMBER, this.getSessionNum());
        ticketComposite.set(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY, this.publicToComposite(this.getDevicePubKey()));

        // ticketComposite.set(Constants.CBOR_R_TICKET_DEVICE_SIGNATURE,
        // this.getSignature());
        ticketComposite.set(Constants.CBOR_R_TICKET_HASH, hash1);
        // ticketComposite.set(Constants.CBOR_R_TICKET_HASH_2, hash2);

        return ticketComposite;
    }

    @Override
    public byte[] toBytes() {
        return toComposite().toBytes();
    }

    @Override
    public Composite toCoseComposite() {
        final byte[] protectedHeader = Composite.newMap()
                .set(Const.COSE_ALG, this.getCoseSignatureAlg())
                .toBytes();

        final byte[] payload = this.toComposite().toBytes();

        return Composite.newArray()
                .set(Const.COSE_SIGN1_PROTECTED, protectedHeader)
                .set(Const.COSE_SIGN1_UNPROTECTED, Composite.newMap())
                .set(Const.COSE_SIGN1_PAYLOAD, payload)
                .set(Const.COSE_SIGN1_SIGNATURE, this.getSignature());
    }

    public void setHash1(byte[] hash) {
        hash1 = hash;
    }

    // public void setHash2(byte[] hash) {
    // hash2 = hash;
    // }

}
