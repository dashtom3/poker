package com.server.service.impl;

import com.server.dao.i.productDao;
import com.server.entity.product.ProductEntity;
import com.server.enums.ErrorCodeEnum;
import com.server.service.i.productService;
import com.server.util.DataWrapper;
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
