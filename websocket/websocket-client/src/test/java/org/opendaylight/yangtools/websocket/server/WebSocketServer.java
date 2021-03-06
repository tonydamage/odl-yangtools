/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.opendaylight.yangtools.websocket.server;

import com.google.common.util.concurrent.SettableFuture;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A HTTP server which serves Web Socket requests at:
 *
 * http://localhost:8080/websocket
 *
 * Open your browser at http://localhost:8080/, then the demo page will be loaded and a Web Socket connection will be
 * made automatically.
 *
 * This server illustrates support for the different web socket specification versions and will work with:
 *
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
 */
public class WebSocketServer implements Runnable {

    /**
     * Utilized to get the port on which the server listens. This is a future as
     * the port is selected dynamically from available ports, thus until the
     * server is started the value will not be established.
     */
    private final SettableFuture<Integer> port;

    /**
     * Maintains the port number with which the class was initialized.
     */
    private final int inPort;
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class.toString());


    public WebSocketServer(final int inPort) {
        this.inPort = inPort;
        port = SettableFuture.<Integer>create();
    }

    @Override
    public void run(){
        try {
            startServer();
        } catch (Exception e) {
            logger.info("Exception occured while starting webSocket server {}",e);
        }
    }

    public Future<Integer> getPort() {
        return port;
    }

    public void startServer() throws Exception {
        try {
            bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new WebSocketServerInitializer());

            Channel ch = bootstrap.bind(inPort).sync().channel();
            SocketAddress localSocket = ch.localAddress();
            try {
                port.set(((InetSocketAddress) localSocket).getPort());
            } catch (ClassCastException cce) {
                throw new ExecutionException("Unknown socket address type", cce);
            }
            logger.info("Web socket server started at port " + port.get() + '.');
            logger.info("Open your browser and navigate to http://localhost:" + port.get() + '/');

            try {
                ch.closeFuture().sync();
            } catch (InterruptedException ie) {
                // No op, sometimes the server is shutdown hard
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
