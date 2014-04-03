package com.tunnelmanager.lib.client;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.commands.ServerCommand;
import com.tunnelmanager.commands.authentication.LoginCommand;
import com.tunnelmanager.utils.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

/**
 * Class TunnelManagerClientHandler
 * client socket
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
@ChannelHandler.Sharable
public class TunnelManagerClientHandler extends ChannelHandlerAdapter {
    /**
     * Netty channel context
     */
    private ChannelHandlerContext context;

    /**
     * Connection
     */
    private TunnelManagerConnection tunnelManagerConnection;

    /**
     * Default constructor
     * @param tunnelManagerConnection connection instance
     */
    public TunnelManagerClientHandler(TunnelManagerConnection tunnelManagerConnection) {
        this.tunnelManagerConnection = tunnelManagerConnection;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        this.context = context;

        send(new LoginCommand(this.tunnelManagerConnection.nextAckId(), ClientManager.getPublicKey(), ClientManager.getApiKey()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                tunnelManagerConnection.connect(tunnelManagerConnection.configureBootstrap(new Bootstrap(), loop));
            }
        }, 3, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if(msg instanceof ServerCommand) {
            ServerCommand command = (ServerCommand) msg;

            Log.v(command.toString());

            this.tunnelManagerConnection.removeAckId(command.getAckId());

            ClientCommand response = command.execute(this.tunnelManagerConnection);

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
