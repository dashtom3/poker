package com.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="api")
public class TestContriller {
	@RequestMapping(value="/test", method = RequestMethod.GET)
    @ResponseBody
    public String getInfo(
    		HttpServletRequest request
    		) {
        return "controller test";
    }
}
