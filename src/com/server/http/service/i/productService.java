package com.server.http.service.i;

import com.server.http.entity.ProductEntity;
import com.server.common.util.DataWrapper;

import java.util.List;

/**
 * Created by joseph on 16/11/4.
 */
public interface productService {

    //add product
    DataWrapper<ProductEntity> addProduct(ProductEntity productEntity);

    //delete product
    DataWrapper<Void> deleteProduct(int productID);

    //get product list
    DataWrapper<List<ProductEntity>> getProductList();
}
