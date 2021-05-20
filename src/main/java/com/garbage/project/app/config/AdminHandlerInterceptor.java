package com.garbage.project.app.config;

import com.garbage.project.model.User;
import com.garbage.project.param.UserLoginInfo;
import com.garbage.project.service.UserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminHandlerInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if(!isAdmin){
            request.setAttribute("msg","NO ACCESS FOR THIS REQUEST");
            request.getRequestDispatcher("/notFound").forward(request,response);
            return false;
        }else{
            return true;
        }
    }
}
