package com.tunnelmanager.server.api.actions;

import com.tunnelmanager.commands.AckCallback;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.tunnel.CreateTunnelCommand;
import com.tunnelmanager.commands.tunnel.CreateTunnelResponseCommand;
import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.api.JsonFactory;
import com.tunnelmanager.server.api.WebServerAction;
import com.tunnelmanager.server.api.WebServerHandler;
import com.tunnelmanager.server.client.ClientHandler;
import com.tunnelmanager.server.ports.PortStatus;
import com.tunnelmanager.server.ports.PortsManager;

import java.sql.SQLException;

/**
 * Class CreateTunnelAction
 * Create a new tunnel
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class CreateTunnelAction extends WebServerAction {
    /**
     * Response flag
     */
    private boolean response;

    /**
     * Response command
     */
    private CreateTunnelResponseCommand commandResponse;

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
        if(checkPost("api_key", "type", "host", "host_port", "data")) {
            String apiKey = getPost("api_key");
            int type = Integer.parseInt(getPost("type"));
            String host = getPost("host");
            int hostPort = Integer.parseInt(getPost("host_port"));
            String data = getPost("data");

            ClientHandler handler = ServerManager.getClient(apiKey);

            if(handler != null && handler.getUser() != null) {
                PortStatus portStatus = PortsManager.getPortStatus(handler.getUser(), data);
                if(portStatus != null) {
                    return JsonFactory.simpleJson("command", "create_tunnel", "message", "executed", "state", "" + portStatus.getState());
                } else {
                    int port = PortsManager.acquirePort(handler.getUser(), data);

                    AckCallback callback = new AckCallback() {
                        @Override
                        public void run(Command command) {
                            if(command instanceof CreateTunnelResponseCommand) {
                                commandResponse = (CreateTunnelResponseCommand) command;
                            }

                            response = true;
                        }
                    };

                    CreateTunnelCommand createTunnelCommand = new CreateTunnelCommand(handler.createAck(callback), type, port, host, hostPort, ServerManager.getSshUserName(), ServerManager.getSshHost());

                    this.response = false;
                    handler.send(createTunnelCommand);

                    for (int i = 0; i < 120; i++) {
                        Thread.sleep(100);
                        if (this.response) {
                            break;
                        }
                    }

                    if(this.response) {
                        if(PortsManager.getPid(port) != null && commandResponse.getTunnelStatus() == CreateTunnelResponseCommand.CONNECTED) {
                            return JsonFactory.simpleJson("command", "create_tunnel", "message", "executed");
                        } else {
                            return JsonFactory.error("create_tunnel", "ssh_error");
                        }
                    } else {
                        return JsonFactory.error("create_tunnel", "timeout");
                    }
                }
            } else {
                return JsonFactory.error("create_tunnel", "api_key_not_found");
            }
        } else {
            return JsonFactory.error("create_tunnel", "syntax_error");
        }
    }
}
