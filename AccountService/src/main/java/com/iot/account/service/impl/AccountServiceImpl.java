package com.iot.account.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iot.account.service.AccountService;
import com.iot.common.util.TextUtil;
import com.iot.dao.UserMapper;
import com.iot.dao.model.User;
import com.iot.dao.model.UserExample;
import com.iot.dao.util.DbConnProxy;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.List;

/**
 * Created by zc on 16-8-9.
 */
public class AccountServiceImpl implements AccountService{

    @Override
    public JSONObject regist(String username, String password) {
        JSONObject json = new JSONObject();
        if(TextUtil.isEmpty(username) || TextUtil.isEmpty(password)){
            json.put("status","cannot be null");
            json.put("code",(byte)3);
            return json;
        }
        SqlSession session = DbConnProxy.getSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        try{
            UserExample example = new UserExample();
            example.or().andUsernameEqualTo(username);
            List<User> users = mapper.selectByExample(example);
            if(users==null || users.isEmpty()){//can regist
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setRegtime(new Date());
                mapper.insert(user);
                json.put("status","ok");
                json.put("code",(byte)1);
            }else{
                json.put("status","user exists");
                json.put("code",(byte)2);
            }
            session.commit();
        }catch (Exception e){
            e.printStackTrace();
            json.put("status","error:"+e.getMessage());
            json.put("code",(byte)4);
            session.rollback();
        }finally {
            DbConnProxy.closeSession();
        }
        return json;
    }

    @Override
    public JSONObject login(String username, String password) {
        JSONObject json = new JSONObject();
        if(TextUtil.isEmpty(username) || TextUtil.isEmpty(password)){
            json.put("status","cannot be null");
            json.put("code",(byte)3);
            return json;
        }
        SqlSession session = DbConnProxy.getSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        try{
            UserExample example = new UserExample();
            example.or().andUsernameEqualTo(username).andPasswordEqualTo(password);
            List<User> users = mapper.selectByExample(example);
            if(users==null || users.isEmpty()){//
                json.put("status","username or password wrong");
                json.put("code",(byte)2);
            }else{
                User user = users.get(0);
                json.put("status","ok");
                json.put("code",(byte)1);
                json.put("group",user.getUsergroup());
                json.put("extraInfo",user.getExtrainfo());
            }
            session.commit();
        }catch (Exception e){
            e.printStackTrace();
            json.put("status","error:"+e.getMessage());
            json.put("code",(byte)4);
            session.rollback();
        }finally {
            DbConnProxy.closeSession();
        }
        return json;
    }

}
