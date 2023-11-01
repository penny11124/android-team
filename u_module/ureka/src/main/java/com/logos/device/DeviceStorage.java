package com.logos.device;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.storage.DeviceStorageInterface;

import org.fidoalliance.fdo.certutils.PemLoader;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

public class DeviceStorage implements DeviceStorageInterface {

    private Composite UTicket;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey ownerPublicKey;
    File file;

    private int sessionNum = 1;

    public DeviceStorage() {
        try {
            String keyString;
            String basePath = "ureka/src/main/java/com/logos/device";

            // file = new File(basePath + "/device_private.pem");
            file = new File(basePath + "/device_private_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.privateKey = PemLoader.loadPrivateKey(keyString);

            // file = new File(basePath + "/device_public.pem");
            file = new File(basePath + "/device_public_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.publicKey = PemLoader.loadPublicKeys(keyString).get(0);

            // file = new File(basePath + "/owner_public.pem");
            file = new File(basePath + "/owner_public_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.ownerPublicKey = PemLoader.loadPublicKeys(keyString).get(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Composite getUTicket() {
        return this.UTicket;
    }

    public String getCipherSuiteName() {
        return Const.AES256_GCM_ALG_NAME;
    }

    public String getKexSuiteName() {
        return Const.ECDH384_ALG_NAME;
    }

    public PrivateKey getDevicePrivateKey() {
        return this.privateKey;
    }

    public PublicKey getOwnerPublicKey() {
        return this.ownerPublicKey;
    }

    public Composite getUTicket(PublicKey publicKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public PrivateKey getHolderPrivateKey(PublicKey publicKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSessionNum() {
        // TODO Auto-generated method stub
        return sessionNum;
    }

    public void storeTicket(Ticket ticket) {
        this.UTicket = ticket.toCoseComposite();

    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public boolean setOwnerPublicKey(PublicKey ownerPublicKey) {
        this.ownerPublicKey = ownerPublicKey;
        return true;
    }

    @Override
    public void addSessionNum() {
        sessionNum++;
    }

    @Override
    public void storeTicket(Composite ticketCose) {
        this.UTicket = ticketCose;
        
    }
}
