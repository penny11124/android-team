package com.example.ureka_voting_machine.ureka;

import com.logos.ticket_module.Communication;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.DispatchResult;


public class UrekaCommunication implements Communication {
    Composite message;

    public UrekaCommunication() {
    }

    @Override
    public DispatchResult sendMessage(Composite message) {
        return null;
    }

    @Override
    public DispatchResult receiveMessage() {
        return new DispatchResult(this.message, false);
    }

    @Override
    public void responseMessage(DispatchResult response) {
        this.message = response.getReply();
    }

    public void setMessage(byte[] message) {
        this.message = Composite.fromObject(message);
    }

    public byte[] getMessage() {
        return message.toBytes();
    }
}
