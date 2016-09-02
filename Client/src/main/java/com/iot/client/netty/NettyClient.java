package com.iot.client.netty;

import com.google.protobuf.ByteString;
import com.iot.client.ClientEnv;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * Created by zc on 16-9-1.
 */
public class NettyClient {

    public static void start(ChannelHandler handler) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    ch.pipeline().addLast(new MyProtobufDecoder());
                    ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast(new MyProtobufEncoder());
                    ch.pipeline().addLast(new IdleStateHandler(5, 5, 5));
                    ch.pipeline().addLast(handler);
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 8888).sync(); // (5)
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class MyProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {

        private final BaseMsg.BaseMsgPb prototype = BaseMsg.BaseMsgPb.getDefaultInstance();

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
                throws Exception {
            final byte[] array;
            final int offset;
            final int length = msg.readableBytes();

            if (!msg.hasArray()) {
                array = new byte[length];
                msg.getBytes(msg.readerIndex(), array, 0, length);
                offset = 0;
            } else {
                array = msg.array();
                offset = msg.arrayOffset() + msg.readerIndex();
            }
            BaseMsg.BaseMsgPb result = prototype.getParserForType().parseFrom(array, offset, length);
            if (result.getIsEncrypt()){
                if(!result.getData().isEmpty()){
                    byte[] decryptedData = CryptUtil.aesDecrypt(result.getData().toByteArray(),ClientEnv.AES_KEY);
                    out.add(result.toBuilder().setData(ByteString.copyFrom(decryptedData)).build());
                    return;
                }
            }
            out.add(result);
        }
    }


    private static class MyProtobufEncoder extends MessageToMessageEncoder<BaseMsg.BaseMsgPbOrBuilder> {
        @Override
        protected void encode(ChannelHandlerContext ctx, BaseMsg.BaseMsgPbOrBuilder msg, List<Object> out)
                throws Exception {

            BaseMsg.BaseMsgPb.Builder result = null;
            if (msg instanceof BaseMsg.BaseMsgPb){
                result = ((BaseMsg.BaseMsgPb)msg).toBuilder();
            }else if (msg instanceof BaseMsg.BaseMsgPb.Builder){
                result = (BaseMsg.BaseMsgPb.Builder)msg;
            }

            if (result.getIsEncrypt()){
                if(!result.getData().isEmpty()){
                    byte[] encryptedData = CryptUtil.aesEncrypt(result.getData().toByteArray(), ClientEnv.AES_KEY);
                    result.setData(ByteString.copyFrom(encryptedData));
                }
            }
            out.add(wrappedBuffer(result.build().toByteArray()));
        }
    }
}