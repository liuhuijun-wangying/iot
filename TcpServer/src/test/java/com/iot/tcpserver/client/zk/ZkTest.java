package com.iot.tcpserver.client.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zc on 16-8-4.
 */
public class ZkTest {

    private static CountDownLatch latch = new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 3000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    System.out.println("================zk conn ok");
                    latch.countDown();
                }
            }
        });
        latch.await();
    }
}
