package com.server.http.service.i;

import org.springframework.stereotype.Service;

/**
 * Created by joseph on 16/11/2.
 */
public interface weChatPayService {

    String getQRCodeUrl(String product_Id);
}