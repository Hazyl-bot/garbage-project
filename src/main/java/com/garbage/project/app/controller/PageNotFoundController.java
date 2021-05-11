package com.garbage.project.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageNotFoundController {
    //TODO: 这里为啥没加载css
    @RequestMapping("/notFound")
    public String notFound(Model model){
        return "404";
    }
}
