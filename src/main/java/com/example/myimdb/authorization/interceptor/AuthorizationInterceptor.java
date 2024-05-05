package com.example.myimdb.authorization.interceptor;

import com.example.myimdb.authorization.annotation.Authorization;
import com.example.myimdb.authorization.manager.TokenManager;
import com.example.myimdb.authorization.model.TokenModel;
import com.example.myimdb.config.Constants;
import com.example.myimdb.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 自定义拦截器，判断此次请求是否有权限
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenManager manager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //如果不需要验证token，直接通过
        if (method.getAnnotation(Authorization.class) == null) {
            return true;
        }

        // 从header中得到token
        String authorization = request.getHeader(Constants.AUTHORIZATION);

        // 验证token
        TokenModel model = manager.getToken(authorization);
        if (manager.checkToken(model)) {
            // 如果token验证成功，将token对应的用户id存在request中，便于之后注入
            request.setAttribute(Constants.CURRENT_USER_ID, model.getUserId());
        } else {
           throw new AuthorizationException("token验证失败");
        }

        return true;
    }

}
