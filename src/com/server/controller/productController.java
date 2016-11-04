package com.server.controller;

import com.server.entity.product.ProductEntity;
import com.server.service.i.productService;
import com.server.util.DataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by joseph on 16/11/4.
 */
@Controller
@RequestMapping(value = "/api/product")
public class productController {
    @Autowired
    private productService iProductService;

    //add product
    @RequestMapping(value = "/add")
    @ResponseBody
    public DataWrapper<ProductEntity> addProduct(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") int price,
            @RequestParam(value = "intro") String intro,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "pic") String pic
    ){
        ProductEntity productEntity=new ProductEntity(id,name,price,intro,type,status,pic);
        return iProductService.addProduct(productEntity);
    }

    //delete product
    @RequestMapping(value = "/delete")
    @ResponseBody
    public DataWrapper<Void> deleteProduct(@RequestParam(value = "id")int productID){
        return iProductService.deleteProduct(productID);
    }

    //get product list
    @RequestMapping(value = "/getList")
    @ResponseBody
    public DataWrapper<List<ProductEntity>> getProductList(){
     return iProductService.getProductList();
    }



}
