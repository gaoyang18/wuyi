package com.wuyi.wyms.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断ThreadLocal中是否有用户
        if(UserHolder.getUser() == null) {
            //没有用户直接拦截并设置状态码
            response.setStatus(401);
            return false;
        }
        return true;
    }

}
