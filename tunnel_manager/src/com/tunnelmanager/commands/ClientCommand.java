package com.tunnelmanager.commands;

import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class ClientCommand
 * Client to Server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public abstract class ClientCommand extends Command {
    public ClientCommand(int ackId) {
        super(ackId);
    }

    /**
     * Execute command (on server)
     * @param handler server handler
     * @return command to send to the client
     */
    public abstract ServerCommand execute(ServerSideHandler handler);
}
