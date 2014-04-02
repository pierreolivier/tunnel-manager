package com.tunnelmanager.server.client;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.commands.authentication.LoginCommand;
import com.tunnelmanager.handlers.ServerSideHandler;
import com.tunnelmanager.server.database.User;
import com.tunnelmanager.server.database.UsersManager;
import com.tunnelmanager.utils.Log;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class ClientHandler
 * client socket
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class ClientHandler extends ChannelHandlerAdapter implements ServerSideHandler {
    /**
     * Current user
     */
    private User user;

    /**
     * ackIds
     */
    private List<Integer> ackIds;

    public ClientHandler() {
        super();

        this.ackIds = new ArrayList<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if(msg instanceof ClientCommand) {
            ClientCommand command = (ClientCommand) msg;

            Log.v("new command : " + command.toString());

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

            Log.v(this.user.toString());

            return true;
        } else {
            return false;
        }
    }

    @Override
    public int nextAckId() {
        Integer ackId;

        synchronized (this.ackIds) {
            Random random = new Random();

            do {
                ackId = random.nextInt(9000) + 1000;
            } while(this.ackIds.contains(ackId));
        }

        return ackId;
    }
}
