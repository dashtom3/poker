package com.server.service.i;

import org.springframework.stereotype.Service;

/**
 * Created by joseph on 16/11/2.
 */
@Service
public interface weChatPayService {

    String getQRCodeUrl(String product_Id);
}
