package com.iot.dispatcher.server;

import com.iot.dispatcher.util.ConfigUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zc on 16-8-5.
 */
public class DispatcherServerMain {

    private static Logger log = LoggerFactory.getLogger(DispatcherServerMain.class);

    public static void main(String[] args) throws InterruptedException {
        int port = getPort();
        start(port);
    }

    private static int getPort(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port");
        return Integer.parseInt(serverPort);
    }

    public static void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new HttpServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            log.info("dispatcher server has started on port "+port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(new DispatcherServerHandler());
        }
    }
}
