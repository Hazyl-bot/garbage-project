package com.garbage.project.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/index.com").setViewName("index");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/main.html").setViewName("index");
        //测试专用
        registry.addViewController("/test.html").setViewName("test");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/css/**","/img/**","/js/**","/scss/**","/vendor/**","/test.html")
                .excludePathPatterns("/user/authenticate","/user/login","/user/forgotPassword","/user/register","/user/registerAction");
        registry.addInterceptor(new AdminHandlerInterceptor()).addPathPatterns("/garbage/**");

    }
}
