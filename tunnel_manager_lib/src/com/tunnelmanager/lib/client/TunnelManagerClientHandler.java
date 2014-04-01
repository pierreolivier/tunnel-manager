package com.tunnelmanager.lib.client;

import com.tunnelmanager.commands.Command;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Class TunnelManagerClientHandler
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class TunnelManagerClientHandler extends ChannelHandlerAdapter {
    /**
     * Netty channel context
     */
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        ctx.close();
    }

    public void send(Command command) {
        if(this.context != null) {
            this.context.writeAndFlush(command);
        }
    }
}