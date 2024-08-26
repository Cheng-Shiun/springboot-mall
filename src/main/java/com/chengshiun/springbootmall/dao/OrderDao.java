package com.chengshiun.springbootmall.dao;

import com.chengshiun.springbootmall.model.Order;
import com.chengshiun.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Order getOrderById(Integer orderId);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    //order table 中需要取得 userId, totalAmount
    Integer insertOrder(Integer userId, Integer totalAmount);

    //order_item table 中需要取得 orderId, productId, quantity, amount
    void insertOrderItems(Integer orderId, List<OrderItem> orderItemList);
}
