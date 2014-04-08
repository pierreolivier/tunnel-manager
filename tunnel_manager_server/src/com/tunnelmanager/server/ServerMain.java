package com.tunnelmanager.server;

import com.tunnelmanager.server.api.WebServer;
import com.tunnelmanager.server.client.ClientHandler;
import com.tunnelmanager.server.ports.PortsManager;
import com.tunnelmanager.server.security.SecurityContextFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Class ServerMain
 * Initialize the server and netty framework
 *
 * @author Pierre-Olivier on 01/04/2014.
 */
public class ServerMain {
    /**
     * Init the server
     *
     * @param args java args
     */
    public void init(String args[]) {
        try {
            ServerManager.loadPropertiesFile();

            ServerManager.updateAuthorizedKeysFile();

            startWebAPISideServer();
            startClientSideServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start client side server
     *
     * Main Loop
     */
    public void startClientSideServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            SSLEngine engine = SecurityContextFactory.getContext().createSSLEngine();
                            engine.setUseClientMode(false);

                            ch.pipeline().addLast("ssl", new SslHandler(engine));
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ClientHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            b.bind(ServerManager.clientPort).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Start web API side server
     *
     * Started into a thread
     */
    public void startWebAPISideServer() throws Exception {
        WebServer webServer = new WebServer();
        webServer.start();
    }
}
