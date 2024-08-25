package com.chengshiun.springbootmall.service.impl;

import com.chengshiun.springbootmall.dao.OrderDao;
import com.chengshiun.springbootmall.dao.ProductDao;
import com.chengshiun.springbootmall.dto.BuyItem;
import com.chengshiun.springbootmall.dto.CreateOrderRequest;
import com.chengshiun.springbootmall.model.OrderItem;
import com.chengshiun.springbootmall.model.Product;
import com.chengshiun.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    //創建訂單 -> 新增 order table, order_item table
    //(使用@Transactional 避免其中一個 table 數據新增失敗)
    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        //該筆訂單的總花費計算
        int totalAmount = 0;

        //訂單清單
        List<OrderItem> orderItemList = new ArrayList<>();

        //所有購買清單中 取出 每項購買商品數據
        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {

            //透過 productId 取得商品數據
            Product product = productDao.getProductById(buyItem.getProductId());

            //商品花費 = 單價(商品數據 中可取得) * 數量(每項 購買商品數據 中可取得)
            int amount = product.getPrice() * buyItem.getQuantity();

            //totalAmount = 訂單總花費
            totalAmount = totalAmount + amount;

            //buyItem -> orderItem
            //將每項 購買商品數據 轉換為 訂單數據
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            //每筆 訂單數據 存入 訂單清單中
            orderItemList.add(orderItem);
        }

        //創建一筆訂單 (取得該筆訂單的 orderId)
        Integer orderId = orderDao.createOrder(userId, totalAmount);


        //同時也創建 訂單詳細記錄 (使用 userId 去關聯)
        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
