package com.tunnelmanager.server.api.actions;

import com.tunnelmanager.commands.tunnel.CreateTunnelCommand;
import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.api.JsonFactory;
import com.tunnelmanager.server.api.WebServerAction;
import com.tunnelmanager.server.api.WebServerHandler;
import com.tunnelmanager.server.client.ClientHandler;

import java.sql.SQLException;

/**
 * Class
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class CreateTunnelAction extends WebServerAction implements Runnable {
    private boolean response;

    /**
     * Default constructor
     *
     * @param handler web server handler
     */
    public CreateTunnelAction(WebServerHandler handler) {
        super(handler);
    }

    @Override
    public String onExecute() throws SQLException, ClassNotFoundException, InterruptedException {
        if(checkPost("api_key", "type", "port", "host", "host_port")) {
            String apiKey = getPost("api_key");
            int type = Integer.parseInt(getPost("type"));
            int port = Integer.parseInt(getPost("port"));
            String host = getPost("host");
            int hostPort = Integer.parseInt(getPost("host_port"));

            ClientHandler handler = ServerManager.getClient(apiKey);

            if(handler != null) {
                CreateTunnelCommand createTunnelCommand = new CreateTunnelCommand(handler.createAck(this), type, port, host, hostPort);

                this.response = false;
                handler.send(createTunnelCommand);

                for (int i = 0; i < 10; i++) {
                    Thread.sleep(100);
                    if (this.response == true) {
                        break;
                    }
                }

                if(this.response) {
                    return JsonFactory.simpleJson("command", "create_tunnel", "message", "executed");
                } else {
                    return JsonFactory.error("create_tunnel", "timeout");
                }
            } else {
                return JsonFactory.error("create_tunnel", "api_key_not_found");
            }
        } else {
            return JsonFactory.error("create_tunnel", "syntax_error");
        }
    }

    @Override
    public void run() {
        this.response = true;
    }
}
