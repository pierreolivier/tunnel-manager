package com.tunnelmanager.commands.tunnel;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;

/**
 * Class CreateTunnelCommand
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class CreateTunnelCommand extends ServerCommand {
    public transient final static int LOCAL = 0;
    public transient final static int REMOTE = 1;

    private int tunnelType;

    private int port;

    private String host;

    private int hostPort;

    public CreateTunnelCommand(int ackId, int tunnelType, int port, String host, int hostPort) {
        super(ackId);
        this.tunnelType = tunnelType;
        this.port = port;
        this.host = host;
        this.hostPort = hostPort;
    }

    @Override
    public ClientCommand execute(ClientSideHandler handler) {
        return new CreateTunnelResponseCommand(this.ackId, 1);
    }

    @Override
    public String toString() {
        return "CreateTunnelCommand{" +
                "tunnelType=" + tunnelType +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", hostPort=" + hostPort +
                '}';
    }
}
