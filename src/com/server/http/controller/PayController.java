package com.server.http.controller;

import com.server.http.service.i.weChatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by joseph on 16/11/2.
 */
@Controller
@RequestMapping(value = "/pay")
public class PayController {

    @Autowired
    private weChatPayService iweChatPayService;

    @RequestMapping(value = "/weChat", method = RequestMethod.POST)
    @ResponseBody
    public String getQRCodeUrl(){
        String productId="123";
        String QRCodeUrl=iweChatPayService.getQRCodeUrl(productId);
        return QRCodeUrl;
    }



}
