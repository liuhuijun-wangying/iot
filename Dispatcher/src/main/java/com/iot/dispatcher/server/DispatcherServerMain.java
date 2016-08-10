package com.iot.dispatcher.server;

import com.alibaba.fastjson.JSON;
import com.iot.common.zk.ZkHelper;
import com.iot.dispatcher.ConsistentHash;
import com.iot.dispatcher.ServerInfo;
import com.iot.dispatcher.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zc on 16-8-5.
 */
public class DispatcherServerMain {

    private static Logger log = LoggerFactory.getLogger(DispatcherServerMain.class);
    //zk存储tcp server data的root path
    private static final String ZK_ROOT_PATH = "/TcpServers";

    public static void main(String[] args) {
        try{
            initZk();
            DispatcherServer.start(getPort());
        }catch (Exception e){
            e.printStackTrace();
            ZkHelper.getInstance().close();
        }
    }

    private static void initZk() throws Exception {
        String zkAddr = ConfigUtil.getProp().getProperty("zk_addr");
        ZkHelper.getInstance().connect(zkAddr,ZK_ROOT_PATH,listener);
        onDataChanged();
    }

    private static void onDataChanged(){
        try{
            List<String> list = ZkHelper.getInstance().getAllData(ZK_ROOT_PATH);
            List<ServerInfo> sis = new ArrayList<>();
            for(String str: list){
                ServerInfo si = JSON.parseObject(str,ServerInfo.class);
                if(si!=null){
                    sis.add(si);
                    log.info("tcp server info changed on zk::"+si.toJsonString());
                }
            }
            ConsistentHash.getInstance().set(sis);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ZkHelper.ChildChangeListener listener = new ZkHelper.ChildChangeListener() {
        @Override
        public void onChanged() {
            onDataChanged();
        }
    };

    private static int getPort(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port");
        return Integer.parseInt(serverPort);
    }

}
