package com.tunnelmanager.commands.authentication;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.handlers.ServerSideHandler;

/**
 * Class LoginCommand
 * Login command, send ssh public key to the server
 *
 * @author Pierre-Olivier on 02/04/2014.
 */
public class LoginCommand extends ClientCommand {
    /**
     * SSH public key
     */
    private final String sshPublicKey;

    /**
     * Api Key
     */
    private final String apiKey;

    /**
     * Default Constructor
     * @param sshPublicKey ssh public key used for the login
     * @param apiKey api key used for the login
     */
    public LoginCommand(ClientSideHandler handler, String sshPublicKey, String apiKey) {
        super(handler);

        this.sshPublicKey = sshPublicKey;
        this.apiKey = apiKey;
    }

    @Override
    public Command execute(ServerSideHandler handler) {
        if(handler.login(this)) {

        } else {

        }

        return null;
    }

    public String getSshPublicKey() {
        return sshPublicKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "LoginCommand{" +
                "ackId='" + ackId + '\'' +
                ", sshPublicKey='" + sshPublicKey + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
