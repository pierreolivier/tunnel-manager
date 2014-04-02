package com.tunnelmanager.server.client;

import com.tunnelmanager.commands.Command;
import com.tunnelmanager.server.database.Database;
import com.tunnelmanager.utils.Log;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Class ClientHandler
 * client socket
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class ClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if(msg instanceof Command) {
            Command command = (Command) msg;

            Log.v("new command : " + command.test);
        }

        context.write(msg);
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
}
