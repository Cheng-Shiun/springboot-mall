package com.chengshiun.springbootmall.controller;

import com.chengshiun.springbootmall.dto.BuyItem;
import com.chengshiun.springbootmall.dto.CreateOrderRequest;
import com.chengshiun.springbootmall.dto.OrderQueryParams;
import com.chengshiun.springbootmall.model.Order;
import com.chengshiun.springbootmall.service.OrderService;
import com.chengshiun.springbootmall.util.OrderPageUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    //建立訂單
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Integer userId,
                                         @RequestBody CreateOrderRequest createOrderRequest) {
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        //getOrderById() -> 取得訂單數據 與訂單清單數據
        Order order = orderService.getFullOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    //查詢訂單列表
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<OrderPageUtil<Order>> getOrders(@PathVariable Integer userId,
                                                 @RequestParam (defaultValue = "10") @Min(0) @Max(100) Integer limit,
                                                 @RequestParam (defaultValue = "0") @Min(0) Integer offset) {
        //將所有前段請求參數放入 dto 傳遞
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        //取得 user 所有的訂單
        List<Order> orderList = orderService.getOrdersByUser(orderQueryParams);

        //取得 訂單總數量
        Integer total = orderService.countOrder(orderQueryParams);

        //分頁
        OrderPageUtil orderPageUtil = new OrderPageUtil();
        orderPageUtil.setLimit(limit);
        orderPageUtil.setOffset(offset);
        orderPageUtil.setQueryTotal(total);
        orderPageUtil.setResult(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(orderPageUtil);
    }
}
