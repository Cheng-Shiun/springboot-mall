package com.chengshiun.springbootmall.service;

import com.chengshiun.springbootmall.dto.CreateOrderRequest;
import com.chengshiun.springbootmall.dto.OrderQueryParams;
import com.chengshiun.springbootmall.model.Order;

import java.util.List;

public interface OrderService {
    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

    Order getFullOrderById(Integer orderId);

    List<Order> getOrdersByUser(OrderQueryParams orderQueryParams);

    Integer countOrder(OrderQueryParams orderQueryParams);
}
