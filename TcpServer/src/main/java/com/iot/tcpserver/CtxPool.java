package com.iot.tcpserver;

import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.Client;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zc on 16-8-29.
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

    private static Map<String,ChannelHandlerContext> clients = new ConcurrentHashMap<>();//clientId -> context

    public static void putClient(String clientId, ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(clientId) || ctx==null){
            return;
        }
        clients.put(clientId,ctx);
    }

    public static void removeClient(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        Client c = ctx.channel().attr(ServerEnv.CLIENT).get();
        if(c!=null){
            clients.remove(c.getId());
        }
    }

    public static ChannelHandlerContext getClient(String id){
        if(TextUtil.isEmpty(id)){
            return null;
        }
        return clients.get(id);
    }
}
