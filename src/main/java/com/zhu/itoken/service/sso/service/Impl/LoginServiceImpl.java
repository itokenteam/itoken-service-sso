package com.zhu.itoken.service.sso.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.zhu.itoken.common.domain.User;
import com.zhu.itoken.service.sso.mapper.UserMapper;
import com.zhu.itoken.service.sso.service.LoginService;
import com.zhu.itoken.service.sso.service.consumer.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    RedisService redisService;

    @Autowired
    UserMapper userMapper;
    @Override
    public User login(String loginCode, String passWord) {
        String s = DigestUtils.md5DigestAsHex(passWord.getBytes());
        User redisUser = null;
        String json = redisService.get(loginCode);
        if (json != null) {
            User user = JSONObject.parseObject(json, User.class);
            if (user.getPassword().equals(s)){
                redisUser = user;
                return redisUser;
            }
        }else {
            User user = new User();
            user.setName(loginCode);
            User user1 = userMapper.selectUser(user);
            if (user1 != null) {
                redisService.put(user1.getName(), JSONObject.toJSON(user1).toString(),300);
                if (s.equals(user1.getPassword())) {
                    redisUser = user1;
                } else {
                    return null;
                }
            }else{
                return null;
            }
        }
        return redisUser;
    }
}
