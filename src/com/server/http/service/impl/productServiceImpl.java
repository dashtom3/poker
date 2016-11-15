package com.server.http.service.impl;

import com.server.http.dao.i.productDao;
import com.server.http.entity.ProductEntity;
import com.server.common.enums.ErrorCodeEnum;
import com.server.http.service.i.productService;
import com.server.common.util.DataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by joseph on 16/11/4.
 */
@Service
public class productServiceImpl implements productService {
    @Autowired
    private productDao iProductDao;

    @Override
    public DataWrapper<ProductEntity> addProduct(ProductEntity productEntity) {
        DataWrapper<ProductEntity> res=new DataWrapper<>();
        if(iProductDao.addProduct(productEntity))
            res.setData(productEntity);
        else
            res.setErrorCode(ErrorCodeEnum.Username_Already_Exist);

        return res;
    }

    @Override
    public DataWrapper<Void> deleteProduct(int productID) {
        DataWrapper<Void> res = new DataWrapper<>();
        if (!iProductDao.deleteProduct(productID))
            res.setErrorCode(ErrorCodeEnum.Error);

        return res;
    }

    @Override
    public DataWrapper<List<ProductEntity>> getProductList() {
        return iProductDao.getProductList();
    }
}
