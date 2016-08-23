package com.iot.common.zk;

import com.iot.common.util.TextUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zc on 16-8-5.
 */
public class ZkHelper {

    private static Logger log = LoggerFactory.getLogger(ZkHelper.class);
    private static final int SESSION_TIMEOUT = 5000;

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

    public interface ChildChangeListener{
        void onChanged();
    }

    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    public void connect(String addr, String rootPath, ChildChangeListener listener) throws Exception {
        if(TextUtil.isEmpty(addr) || TextUtil.isEmpty(rootPath)){
            throw new NullPointerException("addr or path cannot be null");
        }
        zk = new ZooKeeper(addr, SESSION_TIMEOUT, new MyWatcher(listener));
        latch.await();
        initRootPath(rootPath);
    }

    private class MyWatcher implements Watcher{

        private ChildChangeListener listener;
        private MyWatcher(ChildChangeListener listener){
            this.listener = listener;
        }

        @Override
        public void process(WatchedEvent event) {
            if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                if(latch.getCount()==1){
                    log.info("================zk client connect ok");
                    latch.countDown();
                }
            }
            if (listener==null){
                return;
            }
            if(Watcher.Event.EventType.NodeChildrenChanged == event.getType()){
                listener.onChanged();
            }
        }
    }

    private void initRootPath(String rootPath) throws Exception {
        Stat stat = zk.exists(rootPath,false);
        if(stat!=null){//exists
            log.info("zk work root path exists, path="+rootPath);
            return;
        }
        log.info("creating zk work root path, path="+rootPath);
        zk.create(rootPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public List<String> getAllData(String rootPath) throws Exception {
        List<String> childPaths = zk.getChildren(rootPath,true);
        List<String> result = new ArrayList<>();
        if(childPaths.isEmpty()){
            return result;
        }
        for(String childPath: childPaths){
            byte[] data = zk.getData(rootPath+"/"+childPath,false,null);
            if(!TextUtil.isEmpty(data)){
                result.add(new String(data, StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    public void createChildPath(String rootPath, String childPath, byte[] data) throws Exception {
        String path = rootPath+"/"+childPath;
        Stat stat = zk.exists(path,false);
        if(stat!=null){//exists
            zk.delete(path, -1);
        }
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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

}
