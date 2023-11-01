package com.logos.ticket_module.storage;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;

public interface DeviceStorageInterface extends UStorage {
    public abstract Composite getUTicket();

    public abstract String getCipherSuiteName();

    public abstract String getKexSuiteName();

    public abstract PrivateKey getDevicePrivateKey();

    public abstract PublicKey getOwnerPublicKey();

    public abstract boolean setOwnerPublicKey(PublicKey ownerPublicKey);
}
