package com.chengshiun.springbootmall.dao;

import com.chengshiun.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {

    //order table 中需要取得 userId, totalAmount
    Integer createOrder(Integer userId, Integer totalAmount);

    //order_item table 中需要取得 orderId, productId, quantity, amount
    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);
}
