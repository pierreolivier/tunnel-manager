package com.tunnelmanager.lib.client;

import com.tunnelmanager.commands.Command;
import com.tunnelmanager.lib.TunnelManager;
import com.tunnelmanager.utils.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class TunnelManagerConnection
 * Connection manager
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class TunnelManagerConnection {
    /**
     * Tunnel Manager instance
     */
    private final TunnelManager tunnelManager;

    /**
     * Netty event loop group
     */
    private EventLoopGroup group;

    /**
     * Netty channel future instance
     */
    private ChannelFuture channelFuture;

    /**
     * Client handler
     */
    private TunnelManagerClientHandler clientHandler;

    /**
     * Default contructor
     * @param tunnelManager current instance of tunnel manager
     */
    public TunnelManagerConnection(TunnelManager tunnelManager) {
        this.tunnelManager = tunnelManager;
    }

    /**
     * Connect the client to the server
     * This function is blocked while server is connected or something wrong happened
     */
    public void connect() {
        this.group = new NioEventLoopGroup();
        this.clientHandler = new TunnelManagerClientHandler();
        try {
            Bootstrap b = new Bootstrap();
            b.group(this.group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    TunnelManagerConnection.this.clientHandler);
                        }
                    });

            // Make a new connection.
            this.channelFuture = b.connect(this.tunnelManager.getHost(), this.tunnelManager.getPort()).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        this.group.shutdownGracefully();
    }

    public boolean send(Command command) {
        if(this.clientHandler != null) {
            this.clientHandler.send(command);
            return true;
        } else {
            return false;
        }
    }
}
