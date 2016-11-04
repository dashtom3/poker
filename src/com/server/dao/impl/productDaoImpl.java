package com.server.dao.impl;

import com.server.base.BaseDao;
import com.server.dao.i.productDao;
import com.server.entity.product.ProductEntity;
import com.server.util.DataWrapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 16/11/4.
 */
@Repository
public class productDaoImpl extends BaseDao<ProductEntity>  implements productDao{
    @Override
    public boolean addProduct(ProductEntity productEntity) {
        return save(productEntity);
    }

    @Override
    public boolean deleteProduct(int productID) {
        return delete(get(productID));
    }

    @Override
    public DataWrapper<List<ProductEntity>> getProductList() {
        DataWrapper<List<ProductEntity>> res=new DataWrapper<>();
        List<ProductEntity> productList=new ArrayList<>();

        Session session=getSession();
        Criteria criteria=session.createCriteria(ProductEntity.class);
        productList=criteria.list();
        res.setData(productList);

        return res;
    }
}
