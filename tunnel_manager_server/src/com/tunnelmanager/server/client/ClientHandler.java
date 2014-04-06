package com.tunnelmanager.server.client;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.commands.authentication.LoginCommand;
import com.tunnelmanager.handlers.ServerSideHandler;
import com.tunnelmanager.server.ServerManager;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.server.database.UsersManager;
import com.tunnelmanager.utils.Log;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Random;

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
    public HashMap<Integer, Runnable> ackIds;

    public ClientHandler() {
        super();

        this.ackIds = new HashMap<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if(msg instanceof ClientCommand) {
            ClientCommand command = (ClientCommand) msg;

            Log.v("new command : " + command.toString());

            removeAck(command.getAckId());

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
        User user = UsersManager.getUser(command.getSshPublicKey(), command.getApiKey());

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
    public int createAck() {
        return createAck(null);
    }

    @Override
    public int createAck(Runnable runnable) {
        Integer ackId;

        synchronized (this.ackIds) {
            Random random = new Random();

            do {
                ackId = new Integer(random.nextInt(5000) + 5000);
            } while(this.ackIds.containsKey(ackId));

            this.ackIds.put(new Integer(ackId), runnable);
        }

        return ackId;
    }

    @Override
    public void removeAck(int ackId) {
        synchronized (this.ackIds) {
            Runnable runnable = this.ackIds.get(new Integer(ackId));
            if(runnable != null) {
                runnable.run();
            }
            this.ackIds.remove(new Integer(ackId));
        }
    }

    public void send(Command command) {
        if(this.context != null) {
            this.context.writeAndFlush(command);
        }
    }
}
