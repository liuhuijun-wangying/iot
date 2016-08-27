package com.iot.tcpserver;

import com.iot.common.util.TextUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zc on 16-8-27.
 */
public class CtxPool {

    private static Map<String,ChannelHandlerContext> contexts = new ConcurrentHashMap<>();//channelId -> context

    public static void putContext(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        contexts.put(ctx.channel().id().asLongText(),ctx);
    }

    public static ChannelHandlerContext getContext(String id){
        if(TextUtil.isEmpty(id)){
            return null;
        }
        return contexts.get(id);
    }

    public static void removeContext(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        contexts.remove(ctx.channel().id().asLongText());
    }
}
