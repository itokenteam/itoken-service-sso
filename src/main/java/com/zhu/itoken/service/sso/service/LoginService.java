package com.zhu.itoken.service.sso.service;

import com.zhu.itoken.common.domain.User;

public interface LoginService {

    User login(String loginCode, String passWord);
}
