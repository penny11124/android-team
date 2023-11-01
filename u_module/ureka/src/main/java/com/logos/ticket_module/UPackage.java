package com.logos.ticket_module;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;
import org.fidoalliance.fdo.protocol.CryptoService;

public abstract class UPackage {
    private final byte[] protocolVersion;
    private final int sessionNum;
    private byte[] signature;
    private int coseSignatureAlg;
    protected CryptoService cryptoService = new CryptoService();
    protected Composite payload;

    public UPackage(byte[] protocolVersion, int sessionNum) {
        this.protocolVersion = protocolVersion;
        this.sessionNum = sessionNum;
    }

    public UPackage(int sessionNum) {
        this.protocolVersion = Constants.PROTOCOL_VERSION.getBytes();
        this.sessionNum = sessionNum;
    }

    public UPackage(Composite cose) {
        byte[] protectedheader = cose.getAsBytes(Const.COSE_SIGN1_PROTECTED);
        this.coseSignatureAlg = Composite.fromObject(protectedheader).getAsNumber(Const.COSE_ALG).intValue();
        this.signature = cose.getAsBytes(Const.COSE_SIGN1_SIGNATURE);
        this.payload = Composite.fromObject(cose.getAsBytes(Const.COSE_SIGN1_PAYLOAD));
        this.protocolVersion = payload.getAsBytes(Constants.CBOR_TICKET_PROTOCOL_VERSION);
        this.sessionNum = payload.getAsNumber(Constants.CBOR_TICKET_SESSION_NUMBER).intValue();
    }

    public abstract Composite toComposite();

    public abstract Composite toCoseComposite();

    public byte[] getProtocolVersion() {
        return this.protocolVersion;
    }

    public int getSessionNum() {
        return this.sessionNum;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public int getCoseSignatureAlg() {
        return coseSignatureAlg;
    }

    public Composite publicToComposite(PublicKey publicKey) {
        int keyType = this.cryptoService.getPublicKeyType(publicKey);
        return this.cryptoService.encode(publicKey,
                (keyType == Const.PK_RSA2048RESTR)
                        ? Const.PK_ENC_CRYPTO
                        : this.cryptoService.getCompatibleEncoding(publicKey));

    }
}
