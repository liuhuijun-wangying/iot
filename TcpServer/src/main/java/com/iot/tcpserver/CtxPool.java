package com.iot.tcpserver;

import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.Client;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zc on 16-8-29.
 */
public class CtxPool {

    private static Logger logger = LoggerFactory.getLogger(CtxPool.class);

    private static Map<String,ChannelHandlerContext> contexts = new ConcurrentHashMap<>();//channelId -> context

    public static void putContext(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        contexts.put(ctx.channel().id().asLongText(),ctx);
        logger.info("put ctx, size="+contexts.size());
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
        logger.info("rm ctx, size="+contexts.size());
    }

    private static Map<String,ChannelHandlerContext> clients = new ConcurrentHashMap<>();//clientId -> context

    public static void putClient(String clientId, ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(clientId) || ctx==null){
            return;
        }
        clients.put(clientId,ctx);
        logger.info("put client, size="+clients.size());
    }

    public static void removeClient(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        Client c = ctx.channel().attr(ServerEnv.CLIENT).get();
        if(c!=null){
            clients.remove(c.getId());
        }else{
            logger.warn("rm client, client in ctx is null");
        }
        logger.info("rm client, size="+clients.size());
    }

    public static ChannelHandlerContext getClient(String id){
        if(TextUtil.isEmpty(id)){
            return null;
        }
        return clients.get(id);
    }
}
