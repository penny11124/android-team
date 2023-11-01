package com.logos.ticket_module;

import java.security.PublicKey;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.CryptoService;

public class UCryptoService {
    CryptoService cs;

    public UCryptoService(Ticket ticket) {

    }

    public Composite getHolderPublicKey(Composite ticket) {
        return ticket.getAsComposite(Constants.CBOR_U_TICKET_HOLDER_PUBLIC_KEY);
    }

    public void verifyTicket(PublicKey ownerPublicKey) {
    }

    // public Composite getOwnerPublicKey(Composite ticket) {

    // }
}