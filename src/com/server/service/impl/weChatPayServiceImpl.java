package com.server.service.impl;

import com.server.service.i.weChatPayService;
import com.server.util.HttpUtil;
import com.server.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joseph on 16/11/2.
 */
@Service
public class weChatPayServiceImpl implements weChatPayService {


    @Override
    public String getQRCodeUrl(String product_Id) {
        LinkedHashMap<String,Object> par=new LinkedHashMap<>();
        par.put("",product_Id);

        String xmlStr=StringUtil.ToXml(par);
        String url="https://api.mch.weixin.qq.com/pay/unifiedorder";
        String responseStr=HttpUtil.sendPost(url,xmlStr);

        return responseStr;
    }
}
