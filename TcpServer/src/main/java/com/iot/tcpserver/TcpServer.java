package com.iot.tcpserver;

import com.iot.tcpserver.codec.BaseMsgDecoder;
import com.iot.tcpserver.codec.BaseMsgEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {

    private static Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static void startNetty(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(handler);
            b.option(ChannelOption.SO_BACKLOG, 1024);

            ChannelFuture f = b.bind(port).sync();
            log.info("tcp server has started on port "+port);
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static final ChannelHandler handler = new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            ch.pipeline().addLast(new BaseMsgDecoder());
            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
            ch.pipeline().addLast(new BaseMsgEncoder());
            ch.pipeline().addLast(new TcpServerHandler());
        }
    };

}
