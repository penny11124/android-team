package com.logos.ticket_module.storage;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;

public interface UserStorageInterface extends UStorage {

    public abstract Composite getUTicket();

    public abstract Composite getUTicket(PublicKey devicePublicKey);

    public abstract PublicKey getDevPublicKey();
}
