//package com.garbage.project.app.config;
//
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class LoginHandlerInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Object loginUser=request.getSession().getAttribute("loginUser");
//        if(loginUser==null){
//            request.setAttribute("msg","please login first!");
//            request.getRequestDispatcher("/login.html").forward(request,response);
//            return false;
//        }else{
//            return true;
//        }
//    }
//}
