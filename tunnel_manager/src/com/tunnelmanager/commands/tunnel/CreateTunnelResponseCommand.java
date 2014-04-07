package com.tunnelmanager.commands.tunnel;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class CreateTunnelResponseCommand
 * Send create tunnel response to the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class CreateTunnelResponseCommand extends ClientCommand {
    public transient final static int ERROR = 0;
    public transient final static int CONNECTED = 1;

    /**
     * Tunnel status
     */
    private int tunnelStatus;

    /**
     * Default constructor
     * @param ackId ack id
     * @param tunnelStatus tunnel status (error, connected)
     */
    public CreateTunnelResponseCommand(int ackId, int tunnelStatus) {
        super(ackId);

        this.tunnelStatus = tunnelStatus;
    }

    @Override
    public ServerCommand execute(ServerSideHandler handler) {
        return null;
    }

    @Override
    public String toString() {
        return "CreateTunnelResponseCommand{" +
                "tunnelStatus=" + tunnelStatus +
                '}';
    }
}
