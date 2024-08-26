package com.chengshiun.springbootmall.service;

import com.chengshiun.springbootmall.dto.CreateOrderRequest;
import com.chengshiun.springbootmall.model.Order;

public interface OrderService {
    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

    Order getOrderById(Integer orderId);
}
