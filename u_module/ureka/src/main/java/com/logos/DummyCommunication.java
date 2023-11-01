package com.logos;

import com.logos.device.Device;
import com.logos.ticket_module.Communication;

import org.fidoalliance.fdo.protocol.Composite;
import org.fidoalliance.fdo.protocol.DispatchResult;

public class DummyCommunication implements Communication {

    Device device;
    Composite message;

    public DummyCommunication(Device device) {
        this.device = device;
        this.device.setCommunication(this);
        this.device.up();
    }

    public DispatchResult sendMessage(Composite message) {
        this.message = message;
        // System.out.println("send message");
        // System.out.println(Hex.toHexString(this.message.toBytes()));
        // System.out.println("\n");

        boolean result = device.receivePackage();
        return new DispatchResult(this.message, result);
    }

    public DispatchResult receiveMessage() {
        // System.out.println("receive message");
        // System.out.println(Hex.toHexString(this.message.toBytes()));
        // System.out.println("\n");

        return new DispatchResult(this.message, false);
    }

    public void responseMessage(DispatchResult response) {
        this.message = response.getReply();
        // System.out.println("response message");
        // System.out.println(Hex.toHexString(this.message.toBytes()));
        // System.out.println("\n");
    }

}
