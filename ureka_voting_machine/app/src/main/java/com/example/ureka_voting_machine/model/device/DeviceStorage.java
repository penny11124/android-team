package com.example.ureka_voting_machine.model.device;

import android.icu.util.Calendar;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;

import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.UTicket;
import com.logos.ticket_module.storage.DeviceStorageInterface;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class DeviceStorage extends DeviceDB implements DeviceStorageInterface {
    private KeyStore.Entry privateKeyEntry;
    public String UTicket;
    private Ticket ticket;

    private PublicKey ownerPublicKey;
    private int state;// 0: 沒有owner, 1: 有owner

    public DeviceStorage(DeviceDB deviceDB) {
        this.id = deviceDB.id;
        this.session = deviceDB.session;
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            this.privateKeyEntry = ks.getEntry("device", null);

            if (!(this.privateKeyEntry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w("TAG", "Not an instance of a PrivateKeyEntry");
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                kpg.initialize(new KeyGenParameterSpec.Builder(
                        "device",
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA512)
                        .build());

                KeyPair kp = kpg.generateKeyPair();
                ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                this.privateKeyEntry = ks.getEntry("device", null);
            }
            // get owner public key
            Certificate ownerCert = ks.getCertificate("owner");
            if (ownerCert == null) {
                this.state = 0;
                return;
            }
            this.ownerPublicKey = ownerCert.getPublicKey();
            this.state = 1;
        } catch (KeyStoreException kse) {
            // don't have owner public key
            this.state = 0;
            kse.printStackTrace();
        } catch (Exception e) {
            this.state = 0;
            e.printStackTrace();
        }
    }

    public DeviceStorage() {
        super();
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            this.privateKeyEntry = ks.getEntry("device", null);

            if (!(this.privateKeyEntry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w("TAG", "Not an instance of a PrivateKeyEntry");
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                kpg.initialize(new KeyGenParameterSpec.Builder(
                        "device",
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA512)
                        .build());

                KeyPair kp = kpg.generateKeyPair();
                ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                this.privateKeyEntry = ks.getEntry("device", null);
            }
            // get owner public key
            Certificate ownerCert = ks.getCertificate("owner");
            if (ownerCert == null) {
                this.state = 0;
                return;
            }
            this.ownerPublicKey = ownerCert.getPublicKey();
            this.state = 1;
        } catch (KeyStoreException kse) {
            // don't have owner public key
            this.state = 0;
            kse.printStackTrace();
        } catch (Exception e) {
            this.state = 0;
            e.printStackTrace();
        }
    }

    @Override
    public Composite getUTicket() {
        return this.ticket.toCoseComposite();
    }

    public String getCipherSuiteName() {
        return Const.AES256_GCM_ALG_NAME;
    }

    public String getKexSuiteName() {
        return Const.ECDH384_ALG_NAME;
    }

    @Override
    public PrivateKey getDevicePrivateKey() {
        return ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getPrivateKey();
    }

    public PublicKey getOwnerPublicKey() {
        return this.ownerPublicKey;
    }

    public Composite getUTicket(PublicKey publicKey) {
        return this.ticket.toCoseComposite();
    }


    public int getSessionNum() {
        return this.session;
    }

    @Override
    public void addSessionNum() {

    }

    @Override
    public void storeTicket(Ticket ticket) {
        this.ticket = ticket;
        this.uticket = ticket.toBytes();
    }

//    @Override
//    public void storeTicket(Composite composite) {
//        this.ticket = new UTicket(composite);
//        this.uticket = ticket.toBytes();
//    }

    @Override
    public PrivateKey getPrivateKey() {
        return ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getPrivateKey();
    }

    @Override
    public PublicKey getPublicKey() {
        return ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getCertificate().getPublicKey();
    }

    public boolean setOwnerPublicKey(PublicKey ownerPublicKey) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");

            KeyProtection keyProtection = new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_VERIFY)
//                    .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256)
//                    .setRandomizedEncryptionRequired(true)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
//                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                    .setUserAuthenticationRequired(false)
                    .build();
            keyStore.load(null);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(384);
            KeyPair key = keyGen.generateKeyPair();
            PrivateKey pri = key.getPrivate();
            PublicKey pub = key.getPublic();

            // generate certificate
            Calendar start = Calendar.getInstance();
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.YEAR, 10);
            X500Name name = new X500NameBuilder().build();

            X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(name, BigInteger.ONE, start.getTime(), expiry.getTime(), name, SubjectPublicKeyInfo.getInstance(ownerPublicKey.getEncoded()));
            ContentSigner signer = new JcaContentSignerBuilder("SHA384withECDSA").setProvider(new BouncyCastleProvider()).build(pri);
            Certificate cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(signer));


            keyStore.setEntry("owner", new KeyStore.TrustedCertificateEntry(cert), keyProtection);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getState() {
        return state;
    }
}
