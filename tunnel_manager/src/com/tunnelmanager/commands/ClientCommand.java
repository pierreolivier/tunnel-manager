package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class ClientCommand
 * Client to Server
 * @author Pierre-Olivier on 02/04/2014.
 */
public abstract class ClientCommand extends Command {
    public ClientCommand(int ackId) {
        super(ackId);
    }

    public abstract ServerCommand execute(ServerSideHandler handler);
}
