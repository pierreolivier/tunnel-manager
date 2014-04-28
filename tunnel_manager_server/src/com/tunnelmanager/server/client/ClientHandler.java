package com.tunnelmanager.server.client;

import com.tunnelmanager.commands.AckCallback;
import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.commands.authentication.LoginCommand;
import com.tunnelmanager.commands.tunnel.CreateTunnelResponseCommand;
import com.tunnelmanager.handlers.ServerSideHandler;
import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.server.database.UsersDatabaseManager;
import com.tunnelmanager.server.ports.PortStatus;
import com.tunnelmanager.server.ports.PortsManager;
import com.tunnelmanager.utils.Log;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Class ClientHandler
 * client socket
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class ClientHandler extends ChannelHandlerAdapter implements ServerSideHandler {
    /**
     * Netty channel context
     */
    private ChannelHandlerContext context;

    /**
     * Current user
     */
    private User user;

    /**
     * ackIds
     */
    private final HashMap<Integer, AckCallback> ackIds;

    public ClientHandler() {
        super();

        this.ackIds = new HashMap<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if(msg instanceof ClientCommand) {
            ClientCommand command = (ClientCommand) msg;

            Log.v("new command : " + command.toString());

            removeAck(command);

            ServerCommand response = command.execute(this);

            if(response != null) {
                context.writeAndFlush(response);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(this.user != null) {
            PortsManager.releaseAllPorts(this.user);

            ServerManager.removeClient(this.user.getApiKey());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        cause.printStackTrace();

        context.close();
    }

    @Override
    public boolean login(LoginCommand command) {
        User user = UsersDatabaseManager.getUser(command.getSshPublicKey(), command.getApiKey());

        if(user != null) {
            this.user = user;

            ServerManager.addClient(command.getApiKey(), this);

            Log.v(this.user.toString());

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void portBound(final CreateTunnelResponseCommand command) {
        PortsManager.validatePort(this.user, command.getPort());

        scheduleTimeout(command.getPort());
    }

    @Override
    public void releasePort(CreateTunnelResponseCommand command) {
        PortsManager.releasePort(this.user, command.getPort());
    }

    @Override
    public int createAck() {
        return createAck(null);
    }

    @Override
    public int createAck(AckCallback callback) {
        Integer ackId;

        synchronized (this.ackIds) {
            Random random = new Random();

            do {
                ackId = new Integer(random.nextInt(5000) + 5000);
            } while(this.ackIds.containsKey(ackId));

            this.ackIds.put(new Integer(ackId), callback);
        }

        return ackId;
    }

    @Override
    public void removeAck(Command command) {
        synchronized (this.ackIds) {
            AckCallback callback = this.ackIds.get(new Integer(command.getAckId()));
            if(callback != null) {
                callback.run(command);
            }
            this.ackIds.remove(new Integer(command.getAckId()));
        }
    }

    /**
     * Send command to client
     * @param command command
     */
    public void send(Command command) {
        if(this.context != null) {
            this.context.writeAndFlush(command);
        }
    }

    public User getUser() {
        return user;
    }

    /**
     * Schedule tunnel auto close
     * @param port port
     */
    private void scheduleTimeout(final int port) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                PortStatus portStatus = PortsManager.getPortStatus(port);
                if(portStatus != null) {
                    if(!portStatus.isRefresh()) {
                        PortsManager.releasePort(ClientHandler.this.user, port);
                    } else {
                        portStatus.setRefresh(false);
                        scheduleTimeout(port);
                    }
                }
            }
        };

        final EventLoop loop = this.context.channel().eventLoop();
        loop.schedule(r, ServerManager.getTunnelTimeout(), TimeUnit.SECONDS);
    }
}
