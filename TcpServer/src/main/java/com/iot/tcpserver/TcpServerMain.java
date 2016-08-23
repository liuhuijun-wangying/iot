package com.iot.tcpserver;

import com.iot.common.constant.Topics;
import com.iot.common.util.CryptUtil;
import com.iot.common.zk.ZkHelper;
import com.iot.dispatcher.ServerInfo;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.tcpserver.util.ConfigUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Properties;

public class TcpServerMain {

    //zk存储tcp server data的root path
    private static final String ZK_ROOT_PATH = "/TcpServers";

    public static void main(String[] args){
        try{
            ServerInfo si = initConfig();
            initRsaKey();
            initZk(si);
            initKafka();
            TcpServer.startNetty(si.getPort());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseKafkaProducer.getInstance().close();
            BaseKafkaConsumer.getInstance().close();
        }
    }

    private static void initKafka() throws IOException {
        Properties producerProp = new Properties();
        InputStream producerIn = BaseKafkaProducer.class.getClassLoader().getResourceAsStream("producer.properties");
        producerProp.load(producerIn);
        BaseKafkaProducer.getInstance().init(producerProp);

        Properties consumerProp = new Properties();
        InputStream consumerIn = BaseKafkaConsumer.class.getClassLoader().getResourceAsStream("consumer.properties");
        consumerProp.load(consumerIn);
        BaseKafkaConsumer.getInstance().init(consumerProp, new String[]{Topics.TOPIC_SERVICE_RESP},new ServiceRespHandler());
        Thread t = new Thread(BaseKafkaConsumer.getInstance());
        t.start();
    }

    private static void initZk(ServerInfo si) throws Exception {
        String zkAddr = ConfigUtil.getProp().getProperty("zk_addr");
        ZkHelper.getInstance().connect(zkAddr,ZK_ROOT_PATH,null);
        ZkHelper.getInstance().createChildPath(ZK_ROOT_PATH,si.getName(),si.toJsonString().getBytes(StandardCharsets.UTF_8));
    }

    private static void initRsaKey(){
        KeyPair keyPair = CryptUtil.generateKeyPair();
        ServerEnv.PUBLIC_KEY = keyPair.getPublic().getEncoded();
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
