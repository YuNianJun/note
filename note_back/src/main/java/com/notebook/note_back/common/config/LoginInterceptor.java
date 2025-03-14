package com.notebook.note_back.common.config;

import com.notebook.note_back.common.utils.JwtUtil;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //令牌验证
        String token = request.getHeader("Authorization");
        //验证token
        try{
            Map<String,Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true; //放行
        } catch (Exception e){
            //http响应码为401
            response.setStatus(401);
            return false; //不放行
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        ThreadLocalUtil.remove();
    }
}
