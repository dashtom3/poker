package com.server.http.dao.i;

import com.server.http.entity.ProductEntity;
import com.server.common.util.DataWrapper;

import java.util.List;

/**
 * Created by joseph on 16/11/4.
 */
public interface productDao {
    //add product
    boolean addProduct(ProductEntity productEntity);

    //delete product
    boolean deleteProduct(int productID);

    //get product list
    DataWrapper<List<ProductEntity>> getProductList();
}
