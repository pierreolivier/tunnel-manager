package com.tunnelmanager.commands.tunnel;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;

/**
 * Class CreateTunnelCommand
 * Create a ssh tunnel, sent by web api
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class CreateTunnelCommand extends ServerCommand {
    public transient final static int LOCAL = 0;
    public transient final static int REMOTE = 1;

    /**
     * Tunnel type (local, remote)
     */
    private int tunnelType;

    /**
     * Bound port
     */
    private int port;

    /**
     * Destination host
     */
    private String host;

    /**
     * Destination port
     */
    private int hostPort;

    /**
     * Default constructor
     * @param ackId ack id
     * @param tunnelType tunnel type (local, remote)
     * @param port bound port
     * @param host destination host
     * @param hostPort destination port
     */
    public CreateTunnelCommand(int ackId, int tunnelType, int port, String host, int hostPort) {
        super(ackId);
        this.tunnelType = tunnelType;
        this.port = port;
        this.host = host;
        this.hostPort = hostPort;
    }

    @Override
    public ClientCommand execute(ClientSideHandler handler) {
        return new CreateTunnelResponseCommand(this.ackId, CreateTunnelResponseCommand.CONNECTED);
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
