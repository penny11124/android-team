package com.logos.ticket_module;

public class Session {
    public final String protocolId;
    public final int sessionNum;
    public String signature;

    public Session(String protocolId, int sessionNum) {
        this.protocolId = protocolId;
        this.sessionNum = sessionNum;
    }

}
