package com.example.ureka_android.model.user;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;

import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.storage.UserStorageInterface;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class UserStorage extends UserDB implements UserStorageInterface {
    private final String TAG = "UserStorage";
    private KeyStore.Entry privateKeyEntry;
    private PrivateKey privateKey;
    public String UTicket;
    private Ticket ticket;
    private PublicKey ownerPublicKey;
    private PublicKey devicePublicKey;
    private int state;
    private int sessionNum = 0;

    public UserStorage(UserDB userDB) {
        this.id = userDB.id;
        this.sessionNum = userDB.sessionNum;
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                this.privateKey = (PrivateKey) ks.getKey("user", null);
                if (!(this.privateKey instanceof PrivateKey)) {
                    Log.w("TAG", "Not an instance of a PrivateKeyEntry");
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                    kpg.initialize(new KeyGenParameterSpec.Builder(
                            "user",
                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA512)
//                        .setUserAuthenticationRequired(true)
                            .build());

                    KeyPair kp = kpg.generateKeyPair();
                    ks = KeyStore.getInstance("AndroidKeyStore");
                    ks.load(null);
                    this.privateKey = (PrivateKey) ks.getKey("user", null);
                    this.ownerPublicKey = ks.getCertificate("user").getPublicKey();
                }
                this.ownerPublicKey = ks.getCertificate("user").getPublicKey();
            } else {

                this.privateKeyEntry = ks.getEntry("user", null);
                this.privateKey = ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getPrivateKey();
                if (!(this.privateKeyEntry instanceof KeyStore.PrivateKeyEntry)) {
                    Log.w("TAG", "Not an instance of a PrivateKeyEntry");
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                    kpg.initialize(new KeyGenParameterSpec.Builder(
                            "user",
                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA512)
//                        .setUserAuthenticationRequired(true)
                            .build());

                    KeyPair kp = kpg.generateKeyPair();
                    ks = KeyStore.getInstance("AndroidKeyStore");
                    ks.load(null);
                    this.privateKeyEntry = ks.getEntry("user", null);
                    this.ownerPublicKey = ks.getCertificate("user").getPublicKey();
                }
            }

            this.ownerPublicKey = ks.getCertificate("user").getPublicKey();
//            // get owner public key
//            Certificate deviceCert = ks.getCertificate("device");
//            this.ownerPublicKey = deviceCert.getPublicKey();
//            this.state = 1;
        } catch (KeyStoreException kse) {
            // don't have owner public key
            this.state = 0;
//            kse.printStackTrace();
        } catch (Exception e) {
            this.state = 0;
//            e.printStackTrace();
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

    public PublicKey getOwnerPublicKey() {
        return this.ownerPublicKey;
    }

    @Override
    public int getSessionNum() {
        return sessionNum;
    }

    @Override
    public void addSessionNum() {
        sessionNum++;
    }

    public Composite getUTicket(PublicKey publicKey) {
        return this.ticket.toCoseComposite();
    }

//    @Override
//    public PublicKey getDevPublicKey() {
//        return null;
//    }

    public void storeTicket(Ticket ticket) {
        this.ticket = ticket;
    }

//    @Override
//    public void storeTicket(Composite composite) {
//
//    }

    @Override
    public PrivateKey getPrivateKey() {
        return this.privateKey;
//        return ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getPrivateKey();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.ownerPublicKey;
//        return ((KeyStore.PrivateKeyEntry) this.privateKeyEntry).getCertificate().getPublicKey();
    }

    public void setOwnerPublicKey(PublicKey ownerPublicKey) {
        try {
            X509Certificate ownerCert = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(ownerPublicKey.getEncoded()));
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyProtection keyProtection = new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_VERIFY)
//                    .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256)
//                    .setRandomizedEncryptionRequired(true)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
//                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
//                    .setUserAuthenticationRequired(false)
                    .build();
            keyStore.load(null);
            keyStore.setEntry("owner", new KeyStore.TrustedCertificateEntry(ownerCert), keyProtection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getState() {
        return state;
    }

    @Override
    public void failed(Composite request, Composite reply) {
        UserStorageInterface.super.failed(request, reply);
        Log.d(TAG, "failed: ");
    }
}
