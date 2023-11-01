package com.logos.ticket_module;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.DispatchResult;

public interface Communication {
    /**
     * sent message and return response
     * 
     * @param message
     * @return corresponding response
     */
    public abstract DispatchResult sendMessage(Composite message);

    public abstract DispatchResult receiveMessage();

    public abstract void responseMessage(DispatchResult response);
}
