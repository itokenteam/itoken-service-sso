package com.zhu.itoken.service.sso.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhu.itoken.common.domain.User;
import com.zhu.itoken.common.dto.CookieUtils;
import com.zhu.itoken.service.sso.service.LoginService;
import com.zhu.itoken.service.sso.service.consumer.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {
    @Autowired
    RedisService redisService;

    @Autowired
    LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String name, String passWord, @RequestParam(required = false) String url, HttpServletRequest request, HttpServletResponse response){
        User login = loginService.login(name, passWord);
        if (login == null) {
            return "login";
        } else {
            String cookie = UUID.randomUUID().toString();
            String put = redisService.put(cookie, name, 60 * 60 * 24);
            if ("ok".equals(put)) {
                CookieUtils.setCookie(request,response,"token",cookie);
                return "redirect:"+url;
            }
            return "login";
        }
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model,@RequestParam(required = false) String url){
        String token = CookieUtils.getCookieValue(request, "token");
        if (token != null) {
            String s = redisService.get(token);
            if (!StringUtils.isEmpty(s)) {
                String s1 =redisService.get(s);
                if (!StringUtils.isEmpty(s1)) {
                    User user = JSONObject.parseObject(s1, User.class);
                    model.addAttribute("message",user.getName());
                }
            }else{
                model.addAttribute("message","获取登录信息失败");
            }
        }
        if (url != null) {
            model.addAttribute("url", url);
        }
        return "login";
    }
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String login1(){
        return "login";
    }

}
