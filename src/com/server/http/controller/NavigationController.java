package com.server.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by tian on 16/10/9.
 */
@Controller
public class NavigationController {
    @RequestMapping(value="/")
    public String mainPage(){
        return "redirect:/home";
    }
    @RequestMapping(value="/home")
    public String homePage(){
        return "/a";
    }
//    @RequestMapping(value="/test")
//    public String testPage(){
//        return "/a";
//    }
}
