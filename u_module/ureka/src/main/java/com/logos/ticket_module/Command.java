package com.logos.ticket_module;

public class Command {
    private byte[] command;

    public Command(String commandString) {
        this.command = commandString.getBytes();
    }

    public Command(byte[] command) {
        this.command = command;
    }

    public byte[] getByte() {
        return this.command;
    }
}
