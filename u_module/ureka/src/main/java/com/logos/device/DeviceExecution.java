package com.logos.device;

import java.util.Arrays;

import com.logos.ticket_module.Command;
import com.logos.ticket_module.Execution;

public class DeviceExecution implements Execution {

    public void executeCommand(Command command) {
        if (Arrays.equals(command.getByte(), "open".getBytes())) {
            System.out.println("open\n");
        } else {
            System.out.println("not open\n");
        }
    }

}
