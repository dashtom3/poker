package com.server.http.service.impl;

import com.server.http.service.i.weChatPayService;
import com.server.common.util.HttpUtil;
import com.server.common.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

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
