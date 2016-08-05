package com.iot.tcpserver;

import com.iot.common.util.CryptUtil;
import com.iot.common.zk.ZkHelper;
import com.iot.dispatcher.ServerInfo;
import com.iot.tcpserver.util.ConfigUtil;

import java.security.KeyPair;

public class TcpServerMain {

    //zk存储tcp server data的root path
    private static final String ZK_ROOT_PATH = "/TcpServers";

    public static void main(String[] args){
        try{
            ServerInfo si = initConfig();
            initRsaKey();
            initZk(si);
            TcpServer.startNetty(si.getPort());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void initZk(ServerInfo si) throws Exception {
        String zkAddr = ConfigUtil.getProp().getProperty("zk_addr");
        ZkHelper.getInstance().connect(zkAddr,ZK_ROOT_PATH,null);
        ZkHelper.getInstance().createChildPath(ZK_ROOT_PATH,si.getName(),si.toJsonString().getBytes("UTF-8"));
    }

    private static void initRsaKey(){
        KeyPair keyPair = CryptUtil.generateKeyPair();
        ServerEnv.PUBLIC_KEY = CryptUtil.key2Str(keyPair.getPublic());
        ServerEnv.PRIVATE_KEY = keyPair.getPrivate();
    }

    private static ServerInfo initConfig(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port");
        int port = Integer.parseInt(serverPort);
        String serverId = ConfigUtil.getProp().getProperty("server_id");
        String serverIp = ConfigUtil.getProp().getProperty("server_ip");
        return new ServerInfo(serverId,serverIp,port);
    }

}
