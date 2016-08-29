package com.iot.basesvr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.RespCode;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.basesvr.dao.UserMapper;
import com.iot.basesvr.model.User;
import com.iot.basesvr.model.UserExample;
import com.iot.basesvr.service.AccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by zc on 16-8-9.
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private UserMapper userMapper;

    @Override
    public JSONObject regist(String username, String password) throws Exception {
        TextUtil.check(username,password);
        UserExample example = new UserExample();
        example.or().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(example);
        if(users==null || users.isEmpty()){//can regist
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRegtime(new Date());
            userMapper.insert(user);
            return JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
        }else{
            return JsonUtil.buildCommonResp(RespCode.REG_USER_EXISTS,"user exists");
        }
    }

    @Override
    public JSONObject login(String username, String password) throws Exception {
        TextUtil.check(username,password);
        UserExample example = new UserExample();
        example.or().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<User> users = userMapper.selectByExample(example);
        if(TextUtil.isEmpty(users)){
            return JsonUtil.buildCommonResp(RespCode.LOGIN_WRONG_ACCOUNT,"username or password wrong");
        }else{
            User user = users.get(0);
            JSONObject json = JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            json.put("extraInfo",user.getExtrainfo());
            return json;
        }
    }

}
