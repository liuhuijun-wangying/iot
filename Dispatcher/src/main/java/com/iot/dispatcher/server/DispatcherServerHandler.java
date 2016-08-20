package com.iot.dispatcher.server;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.RespCode;
import com.iot.common.util.TextUtil;
import com.iot.dispatcher.ConsistentHash;
import com.iot.dispatcher.ServerInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by zc on 16-8-5.
 */
public class DispatcherServerHandler extends ChannelInboundHandlerAdapter {

    private static final String KEY_ID = "id";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private String parseId(HttpRequest req){
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            List<String> strs = params.get(KEY_ID);
            if(strs!=null && strs.size()==1){
                return strs.get(0);
            }
        }
        return null;
    }

    private String getRespResult(String clientId){
        JSONObject jsonObject = new JSONObject();
        if(TextUtil.isEmpty(clientId)){
            jsonObject.put("msg","param invalid");
            jsonObject.put("code", RespCode.COMMON_INVALID);//1:client param error
            return jsonObject.toJSONString();
        }
        ServerInfo si = ConsistentHash.getInstance().get(clientId);
        if(si==null){
            jsonObject.put("msg","no available server");
            jsonObject.put("code",RespCode.COMMON_EXCEPTION);//2:server error
            return jsonObject.toJSONString();
        }
        jsonObject.put("msg","ok");
        jsonObject.put("code",RespCode.COMMON_OK);//0:success
        jsonObject.put("ip",si.getIp());
        jsonObject.put("port",si.getPort());
        return jsonObject.toJSONString();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            byte[] result = null;
            try {
                result = getRespResult(parseId(req)).getBytes("UTF-8");
            } catch (UnsupportedEncodingException cannotHappen) {
            }

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(result));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(req);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
