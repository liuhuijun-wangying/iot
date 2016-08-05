package com.iot.dispatcher;

import com.iot.common.util.CryptUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by zc on 16-8-5.
 */
public class ConsistentHash {

    private static final int REPLICAS_NUM = 150;
    private SortedMap<Integer,ServerInfo> circle = new TreeMap<>();
    private List<ServerInfo> members = new ArrayList<>();

    public synchronized void set(ServerInfo[] sis){
        circle.clear();
        members.clear();
        for(ServerInfo si: sis){
            _add(si);
        }
    }

    public synchronized void add(ServerInfo si){
        _add(si);
    }

    public synchronized void remove(ServerInfo si) {
        for(int i=0;i<REPLICAS_NUM;i++){
            circle.remove(hash(getKey(si.getName(),i)));
        }
        members.remove(si);
    }

    public synchronized ServerInfo get(String id){
        if(circle.isEmpty()){
            return null;
        }
        int hashKey = hash(id);
        if (!circle.containsKey(hashKey)) {
            SortedMap<Integer, ServerInfo> tailMap = circle.tailMap(hashKey);
            hashKey = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hashKey);
    }

    private void _add(ServerInfo si) {
        for(int i=0;i<REPLICAS_NUM;i++){
            circle.put(hash(getKey(si.getName(),i)),si);
        }
        members.add(si);
    }

    private String getKey(String elt, int idx) {
        return elt+idx;
    }

    private int hash(String key){
        return CryptUtil.md5(key).hashCode();
    }

}
