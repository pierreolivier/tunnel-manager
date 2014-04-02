package com.tunnelmanager.lib;

import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.LoginCommand;
import com.tunnelmanager.lib.client.ClientManager;
import com.tunnelmanager.lib.client.TunnelManagerConnection;
import com.tunnelmanager.utils.Log;

/**
 * Class TunnelManager
 * Entry point of the library
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class TunnelManager {
    /**
     * Server host
     */
    private final String host;

    /**
     * Server port
     */
    private final int port;

    /**
     * Current connection
     */
    private TunnelManagerConnection tunnelManagerConnection;

    /**
     * Default constructor
     * @param host server host
     * @param port server client side port
     */
    public TunnelManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void initialize() {
        // Load configuration
        ClientManager.loadPropertiesFile();

        // Init connection
        this.tunnelManagerConnection = new TunnelManagerConnection(this);
        this.tunnelManagerConnection.connect();

        // Connection
        this.tunnelManagerConnection.send(new LoginCommand(ClientManager.getPublicKey()));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
