package com.iot.tcpserver.client;

import com.iot.common.util.TextUtil;
import com.iot.tcpserver.ServerEnv;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zc on 16-8-10.
 */
public class ClientManager {

    private ClientManager(){}
    private static ClientManager instance;
    public static ClientManager getInstance(){
        if(instance==null){
            synchronized (ClientManager.class){
                if (instance==null){
                    instance = new ClientManager();
                }
            }
        }
        return instance;
    }

    //private Map<String,Client> devices = new HashMap<>();
    private Map<String,List<ChannelHandlerContext>> apps = new ConcurrentHashMap<>();
    private Map<String,ChannelHandlerContext> contexts = new ConcurrentHashMap<>();
    //private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void putContext(ChannelHandlerContext ctx){
        if(ctx==null){
            return;
        }
        contexts.put(ctx.channel().id().asLongText(),ctx);
    }

    public ChannelHandlerContext getContext(String id){
        if(TextUtil.isEmpty(id)){
            return null;
        }
        return contexts.get(id);
    }

    public void removeContext(String id){
        if(TextUtil.isEmpty(id)){
            return;
        }
        contexts.remove(id);
    }

    public void onLogin(String username, ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(username)){
            return;
        }
        List<ChannelHandlerContext> list = apps.get(username);
        if(list!=null){
            /*for(ChannelHandlerContext oldCtx: list){
                if(oldCtx.channel().attr(ServerEnv.ID).get().equals(c.getClientId())){
                    //不需要再close了，上面已经close过了，同一个对象
                    appClients.remove(oldAppClient);
                    break;
                }
            }*/
            list.add(ctx);
        }else{
            list = new ArrayList<>();
            list.add(ctx);
            apps.put(username,list);
        }
    }

    public void onLogout(String username, String clientId){
        if(TextUtil.isEmpty(username)){
            return;
        }
        List<ChannelHandlerContext> list = apps.get(username);
        if(list==null){
            return;
        }
        for(ChannelHandlerContext ctx: list){
            if(ctx.channel().attr(ServerEnv.ID).get().equals(clientId)){
                list.remove(ctx);
                break;
            }
        }
        if(list.isEmpty()){
            apps.remove(username);
        }
    }

    /*//设备上线
    public boolean putClient(DeviceClient c){
        if(c==null){
            return false;
        }
        if (TextUtil.isEmpty(c.getClientId())){
            return false;
        }
        lock.writeLock().lock();
        try{
            Client oldClient = devices.get(c.getClientId());
            if(oldClient!=null){//close old one
                oldClient.close();
            }
            devices.put(c.getClientId(),c);
            return true;
        }finally {
            lock.writeLock().unlock();
        }
    }
    //app登录
    public boolean putClient(AppClient c){
        if(c==null){
            return false;
        }
        if (TextUtil.isEmpty(c.getClientId())){
            return false;
        }
        if(TextUtil.isEmpty(c.getUsername())){
            return false;
        }
        lock.writeLock().lock();
        try{
            Client oldClient = devices.get(c.getClientId());
            if(oldClient!=null){//close old one
                oldClient.close();
            }
            devices.put(c.getClientId(),c);
            List<Client> appClients = apps.get(c.getUsername());
            if(appClients!=null){
                for(Client oldAppClient: appClients){
                    if(oldAppClient.getClientId().equals(c.getClientId())){
                        //不需要再close了，上面已经close过了，同一个对象
                        appClients.remove(oldAppClient);
                        break;
                    }
                }
                appClients.add(c);
            }else{
                appClients = new ArrayList<>();
                appClients.add(c);
                apps.put(c.getUsername(),appClients);
            }
            return true;
        }finally {
            lock.writeLock().unlock();
        }
    }

    //设备下线
    public void removeClient(String clientId){
        if(TextUtil.isEmpty(clientId)){
            return;
        }
        lock.writeLock().lock();
        try{
            Client c = devices.get(clientId);
            if(c==null){
                return;
            }
            if(c instanceof AppClient){
                removeAppClient(((AppClient) c).getUsername(),clientId);
            }
            devices.remove(clientId);
        }finally {
            lock.writeLock().unlock();
        }
    }
    private void removeAppClient(String username, String clientId){
        if(TextUtil.isEmpty(username)){
            return;
        }
        List<Client> list = apps.get(username);
        if(list==null){
            return;
        }
        for(Client c: list){
            if(c.getClientId().equals(clientId)){
                list.remove(c);
                break;
            }
        }
        if(list.isEmpty()){
            apps.remove(username);
        }
    }

    public int getAppClientNum(String username){
        if(TextUtil.isEmpty(username)){
            return 0;
        }
        lock.readLock().lock();
        try{
            List<Client> list = apps.get(username);
            if(list==null){
                return 0;
            }
            return list.size();
        }finally {
            lock.readLock().unlock();
        }
    }

    public List<Client> getAppClients(String username){
        if(TextUtil.isEmpty(username)){
            return null;
        }
        lock.readLock().lock();
        try{
            return apps.get(username);
        }finally {
            lock.readLock().unlock();
        }
    }

    public Client getClient(String clientId){
        if(TextUtil.isEmpty(clientId)){
            return null;
        }
        lock.readLock().lock();
        try{
           return devices.get(clientId);
        }finally {
            lock.readLock().unlock();
        }
    }*/
}
