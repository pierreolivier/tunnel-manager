package com.tunnelmanager.lib.client;

import com.tunnelmanager.commands.ClientCommand;
import com.tunnelmanager.commands.Command;
import com.tunnelmanager.handlers.ClientSideHandler;
import com.tunnelmanager.lib.TunnelManager;
import com.tunnelmanager.lib.client.security.SecurityContextFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Class TunnelManagerConnection
 * Connection manager
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class TunnelManagerConnection implements ClientSideHandler {
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
     * ackIds
     */
    public final HashMap<Integer, Runnable> ackIds;

    /**
     * Default constructor
     * @param tunnelManager current instance of tunnel manager
     */
    public TunnelManagerConnection(TunnelManager tunnelManager) {
        super();

        this.tunnelManager = tunnelManager;

        this.clientHandler = new TunnelManagerClientHandler(this);
        this.ackIds = new HashMap<>();
    }

    /**
     * Connect the client to the server
     * This function is blocked while server is connected or something wrong happened
     */
    public void connect() {
        configureBootstrap(new Bootstrap())
                .connect(this.tunnelManager.getHost(), this.tunnelManager.getPort())
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            final EventLoop loop = future.channel().eventLoop();
                            loop.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    connect(configureBootstrap(new Bootstrap(), loop));
                                }
                            }, 3, TimeUnit.SECONDS);
                        }
                    }
                });
    }

    /**
     * Connect override
     * @param bootstrap bootstrap to use for the connection
     */
    public void connect(Bootstrap bootstrap) {
        bootstrap
                .connect(this.tunnelManager.getHost(), this.tunnelManager.getPort())
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            final EventLoop loop = future.channel().eventLoop();
                            loop.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    connect(configureBootstrap(new Bootstrap(), loop));
                                }
                            }, 3, TimeUnit.SECONDS);
                        }
                    }
                });
    }

    /**
     * Create a new bootstrap
     * @param b bootstrap
     * @return configured bootstrap
     */
    private Bootstrap configureBootstrap(Bootstrap b) {
        return configureBootstrap(b, new NioEventLoopGroup());
    }

    /**
     * Configure a new bootstrap
     * @param bootstrap bootstrap
     * @param group group
     * @return configured bootstrap
     */
    public Bootstrap configureBootstrap(Bootstrap bootstrap, EventLoopGroup group) {
        this.group = group;
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        SSLEngine engine = SecurityContextFactory.getContext().createSSLEngine();
                        engine.setUseClientMode(true);

                        ch.pipeline().addLast("ssl", new SslHandler(engine));
                        ch.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                TunnelManagerConnection.this.clientHandler);
                    }
                });
        return bootstrap;
    }

    /**
     * Close gracefully connection
     */
    public void close() throws InterruptedException {
        this.group.shutdownGracefully();
    }

    /**
     * Send command to server
     * @param command command
     * @return true if command sent else false
     */
    public boolean send(ClientCommand command) {
        if(this.clientHandler != null) {
            this.clientHandler.send(command);
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
                ackId = new Integer(random.nextInt(4000) + 1000);
            } while(this.ackIds.containsKey(ackId));

            this.ackIds.put(new Integer(ackId), runnable);
        }

        return ackId;
    }

    @Override
    public void removeAck(Command command) {
        synchronized (this.ackIds) {
            Runnable runnable = this.ackIds.get(new Integer(command.getAckId()));
            if(runnable != null) {
                runnable.run();
            }
            this.ackIds.remove(new Integer(command.getAckId()));
        }
    }

    @Override
    public void onLoginResponse(int status) {
        if(this.tunnelManager.getTunnelManagerHandler() != null) {
            this.tunnelManager.getTunnelManagerHandler().onLoginResponse(status);
        }
    }

    @Override
    public String getPrivateKeyPath() {
        return ClientManager.getPrivateKeyPath();
    }
}
