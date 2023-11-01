package com.logos.ticket_module.module_interface;

import java.security.PublicKey;

import com.logos.ticket_module.Command;

public interface DeviceModuleInterface {
    public abstract Command acceptTicket();

    public abstract void sign();

    public abstract void verifyTicket(PublicKey publicKey);

    public abstract void verifyEncryptedPackage();

    public abstract void addHash();

    public abstract void addHash2();

    public abstract void addData();

    public abstract void addInfo();

    public abstract void issue();

    public abstract boolean receivePackage();

    public abstract void doCommand();

    public abstract PublicKey getPublicKey();

    public abstract void getRTicket();

}
