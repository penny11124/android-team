package com.logos.ticket_module;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.certutils.PemLoader;

public abstract class Ticket extends UPackage {
    private final PublicKey devicePubKey;

    public Ticket(int sessionNum, String devicePubKey) {
        super(sessionNum);
        this.devicePubKey = PemLoader.loadPublicKeys(devicePubKey).get(0);
    }

    public Ticket(int sessionNum, PublicKey devicePubKey) {
        super(sessionNum);
        this.devicePubKey = devicePubKey;
    }

    // public Ticket(Composite tickeComposite) {
    // super(tickeComposite.getAsString(
    // Constants.CBOR_TICKET_PROTOCOL_VERSION),
    // tickeComposite.getAsNumber(Constants.CBOR_TICKET_SESSION_NUMBER).intValue());
    // this.devicePubKey = cryptoService
    // .decode(tickeComposite.getAsComposite(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY));
    // }

    public Ticket(Composite cose) {
        super(cose);
        this.devicePubKey = this.cryptoService
                .decode(this.payload.getAsComposite(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY));

    }

    public abstract Composite toComposite();

    public abstract Composite toCoseComposite();
    // {
    // Composite ticketComposite = Composite.newArray();
    // ticketComposite.set(Constants.CBOR_TICKET_PROTOCOL_VERSION, protocolVersion);
    // ticketComposite.set(Constants.CBOR_TICKET_SESSION_NUMBER, sessionNum);
    // ticketComposite.set(Constants.CBOR_TICKET_DEVICE_PUBLIC_KEY, devicePubKey);
    // return ticketComposite;
    // }

    public abstract byte[] toBytes();

    // {
    // ByteArrayOutputStream bao = new ByteArrayOutputStream();
    // WritableByteChannel wbc = Channels.newChannel(bao);
    // Encoder encoder = new Encoder.Builder(wbc).build();
    // try {
    // encoder.writeObject(this);
    // return bao.toByteArray();
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }
    public PublicKey getDevicePubKey() {
        return this.devicePubKey;
    }
}
