package com.logos.user;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.logos.ticket_module.Ticket;
import com.logos.ticket_module.storage.UserStorageInterface;

import org.fidoalliance.fdo.certutils.PemLoader;
import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.Const;

public class UserStorage implements UserStorageInterface {
    int sessionNum = 1;
    Ticket ticket;
    PrivateKey privateKey;
    PublicKey publicKey;
    PublicKey devicePublicKey;
    File file;
    Composite ticketCose;

    public UserStorage() {
        try {
            String keyString;
            String basePath = "ureka/src/main/java/com/logos/user";
            // file = new File(basePath + "/owner_private.pem");
            file = new File(basePath + "/owner_private_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.privateKey = PemLoader.loadPrivateKey(keyString);

            // file = new File(basePath + "/owner_public.pem");
            file = new File(basePath + "/owner_public_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.publicKey = PemLoader.loadPublicKeys(keyString).get(0);

            // file = new File(basePath + "/device_public.pem");
            file = new File(basePath + "/device_public_256.pem");
            keyString = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
            this.devicePublicKey = PemLoader.loadPublicKeys(keyString).get(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getCipherSuiteName() {
        return Const.AES256_GCM_ALG_NAME;
    }

    public String getKexSuiteName() {
        return Const.ECDH384_ALG_NAME;
    }

    public PublicKey getOwnerPublicKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSessionNum() {
        return this.sessionNum;
    }

    public void storeTicket(Ticket ticket) {
        this.ticket = ticket;
        this.ticketCose = ticket.toCoseComposite();
    }

    public Composite getUTicket() {
        // return this.ticket.toCoseComposite();
        return this.ticketCose;
    }

    public Composite getUTicket(PublicKey devicePublicKey) {
        // return this.ticket.toCoseComposite();
        return this.ticketCose;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public void addSessionNum() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void storeTicket(Composite ticketCose) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public PublicKey getDevPublicKey() {
        // TODO Auto-generated method stub
        return null;
    }

}
