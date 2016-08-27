package com.iot.basesvr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.RespCode;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.basesvr.service.IMService;
import org.springframework.stereotype.Service;

/**
 * Created by zc on 16-8-26.
 */
@Service
public class IMServiceImpl implements IMService {

    @Override
    public JSONObject addFriend(String from, String to) {
        if(TextUtil.isEmpty(from) || TextUtil.isEmpty(to)){
            return JsonUtil.buildCommonResp(RespCode.COMMON_INVALID,"target is null");
        }

        return null;
    }
}
