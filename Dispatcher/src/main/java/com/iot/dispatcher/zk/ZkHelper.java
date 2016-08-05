package com.iot.dispatcher.zk;

import com.iot.dispatcher.util.ConfigUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zc on 16-8-5.
 */
public class ZkHelper {

    //test
    public static void main(String[] args) throws Exception {
        ZkHelper.getInstance().connect();
        ZkHelper.getInstance().test();
    }

    private static Logger log = LoggerFactory.getLogger(ZkHelper.class);
    private static final String RootPath = "/TcpServers";

    private ZkHelper(){}
    private static ZkHelper instance;
    public static ZkHelper getInstance(){
        if(instance==null){
            synchronized (ZkHelper.class){
                if(instance==null){
                    instance = new ZkHelper();
                }
            }
        }
        return instance;
    }

    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    public void connect() throws Exception {
        String addr = getZkAddr();
        zk = new ZooKeeper(addr, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    log.info("===zk conn ok on addr::"+addr);
                    latch.countDown();
                }
            }
        });
        latch.await();
    }

    private void initPath(){

    }

    public void close(){
        if(zk!=null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void test() throws Exception {
        String testRootPath = "/testRootPath";

        Stat stat = zk.exists(testRootPath,false);
        System.out.println("exists::"+stat==null);






        /*zk.create("/testRootPath", "testRootData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

        // 创建一个子目录节点
        zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println("==getData::"+new String(zk.getData("/testRootPath",false,null)));

        // 取出子目录节点列表
        System.out.println("==getChild::"+zk.getChildren("/testRootPath",true));

        // 创建另外一个子目录节点
        zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println(zk.getChildren("/testRootPath",true));

        // 修改子目录节点数据
        zk.setData("/testRootPath/testChildPathOne","hahahahaha".getBytes(),-1);
        byte[] datas = zk.getData("/testRootPath/testChildPathOne", true, null);
        String str = new String(datas,"utf-8");
        System.out.println(str);

        //删除整个子目录   -1代表version版本号，-1是删除所有版本
        zk.delete("/testRootPath/testChildPathOne", -1);
        System.out.println(zk.getChildren("/testRootPath",true));
        System.out.println("==getData::"+new String(zk.getData("/testRootPath",false,null)));*/
    }

    private String getZkAddr(){
        return ConfigUtil.getProp().getProperty("zk_addr");
    }
}
