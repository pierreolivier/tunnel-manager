package com.tunnelmanager.commands.tunnel;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.process.SSHProcess;
import com.tunnelmanager.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
     * Username used for ssh connection
     */
    private String sshUserName;

    /**
     * Host used for ssh connection
     */
    private String sshHost;

    /**
     * Default constructor
     * @param ackId ack id
     * @param tunnelType tunnel type (local, remote)
     * @param port bound port
     * @param host destination host
     * @param hostPort destination port
     * @param sshUserName username for ssh connection
     * @param sshHost ssh host server
     */
    public CreateTunnelCommand(int ackId, int tunnelType, int port, String host, int hostPort, String sshUserName, String sshHost) {
        super(ackId);
        this.tunnelType = tunnelType;
        this.port = port;
        this.host = host;
        this.hostPort = hostPort;
        this.sshUserName = sshUserName;
        this.sshHost = sshHost;
    }



    @Override
    public ClientCommand execute(ClientSideHandler handler) {
        String type = (this.tunnelType == LOCAL ? "-L" : "-R");

        SSHProcess process = new SSHProcess("-oStrictHostKeyChecking=no -i " + handler.getPrivateKeyPath() + " " + type + " " + this.port + ":" + this.host + ":" + this.hostPort + " " + this.sshUserName + "@" + this.sshHost);
        boolean connected = process.waitTunnel();

        if(connected) {
            return new CreateTunnelResponseCommand(this.ackId, CreateTunnelResponseCommand.CONNECTED, this.port);
        } else {
            return new CreateTunnelResponseCommand(this.ackId, CreateTunnelResponseCommand.ERROR, this.port);
        }
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
