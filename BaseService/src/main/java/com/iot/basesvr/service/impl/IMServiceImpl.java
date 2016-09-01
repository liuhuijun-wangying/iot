package com.iot.basesvr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iot.basesvr.dao.FriendUserDeviceMapper;
import com.iot.basesvr.model.FriendUserDevice;
import com.iot.basesvr.model.FriendUserDeviceExample;
import com.iot.common.constant.RespCode;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.basesvr.service.IMService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zc on 16-8-26.
 */
@Service
public class IMServiceImpl implements IMService {

    @Resource
    private FriendUserDeviceMapper friendUserDeviceMapper;
    @Resource(name = "redisTemplate")
    private HashOperations<String,String,String> hashOperations;

    @Override
    public JSONObject addDevice(String username, String deviceId) throws Exception {
        FriendUserDevice friendUserDevice = selectFriendUserDevice(username,deviceId);
        if (friendUserDevice == null){
            friendUserDevice = new FriendUserDevice();
            friendUserDevice.setDeviceId(deviceId);
            friendUserDevice.setUsername(username);
            friendUserDevice.setStatus(true);
            friendUserDevice.setUpdatetime(new Date());
            friendUserDeviceMapper.insert(friendUserDevice);
            return JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
        }else{
            if (friendUserDevice.getStatus()){//is friend
                return JsonUtil.buildCommonResp(RespCode.ADD_FRIEND_ALREADY,"you are friends already");
            }else{
                friendUserDevice.setStatus(true);
                friendUserDevice.setUpdatetime(new Date());
                friendUserDeviceMapper.updateByPrimaryKey(friendUserDevice);
                return JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            }
        }
    }

    @Override
    public JSONObject delDevice(String username, String deviceId) throws Exception {
        FriendUserDevice friendUserDevice = selectFriendUserDevice(username,deviceId);
        if (friendUserDevice == null){
            return JsonUtil.buildCommonResp(RespCode.DEL_FRIEND_NOT_EXISTS,"you are not friends");
        }else{
            if (friendUserDevice.getStatus()){//is friend
                friendUserDevice.setStatus(false);
                friendUserDevice.setUpdatetime(new Date());
                friendUserDeviceMapper.updateByPrimaryKey(friendUserDevice);
                return JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            }else{
                return JsonUtil.buildCommonResp(RespCode.DEL_FRIEND_NOT_EXISTS,"you are not friends");
            }
        }
    }

    @Override
    public void putImMsg(String redisKey, String msgKey, String msgValue) {
        //TODO expire
        if (hashOperations.hasKey(redisKey,msgKey)){
            //avoid repeat msg
            return;
        }
        hashOperations.put(redisKey,msgKey,msgValue);
    }

    @Override
    public Long removeImMsg(String redisKey, String... msgKeys) {
        return hashOperations.delete(redisKey,msgKeys);
    }

    @Override
    public Map<String, String> getAllImMsg(String redisKey) {
        return hashOperations.entries(redisKey);
    }

    private FriendUserDevice selectFriendUserDevice(String username, String deviceId){
        TextUtil.check(username,deviceId);
        FriendUserDeviceExample example = new FriendUserDeviceExample();
        example.or().andDeviceIdEqualTo(deviceId).andUsernameEqualTo(username);
        List<FriendUserDevice> list = friendUserDeviceMapper.selectByExample(example);
        if (TextUtil.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }
}
