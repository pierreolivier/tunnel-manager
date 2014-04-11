package com.tunnelmanager.server.api.actions;

import com.tunnelmanager.commands.tunnel.CreateTunnelCommand;
import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.api.JsonFactory;
import com.tunnelmanager.server.api.WebServerAction;
import com.tunnelmanager.server.api.WebServerHandler;
import com.tunnelmanager.server.client.ClientHandler;
import com.tunnelmanager.server.ports.PortStatus;
import com.tunnelmanager.server.ports.PortsManager;

import java.sql.SQLException;

/**
 * Class RefreshTunnelAction
 * Refresh an existing tunnel
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class RefreshTunnelAction extends WebServerAction {

    /**
     * Default constructor
     *
     * @param handler web server handler
     */
    public RefreshTunnelAction(WebServerHandler handler) {
        super(handler);
    }

    @Override
    public String onExecute() throws SQLException, ClassNotFoundException, InterruptedException {
        if(checkPost("api_key", "data")) {
            String apiKey = getPost("api_key");
            String data = getPost("data");

            ClientHandler handler = ServerManager.getClient(apiKey);

            if(handler != null && handler.getUser() != null) {
                PortStatus portStatus = PortsManager.getPortStatus(handler.getUser(), data);

                if(portStatus != null) {
                    portStatus.setRefresh(true);

                    return JsonFactory.simpleJson("command", "refresh_tunnel", "message", "executed");
                } else {
                    return JsonFactory.error("refresh_tunnel", "tunnel_not_found");
                }
            } else {
                return JsonFactory.error("refresh_tunnel", "api_key_not_found");
            }
        } else {
            return JsonFactory.error("refresh_tunnel", "syntax_error");
        }
    }
}
