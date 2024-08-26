package com.chengshiun.springbootmall.service.impl;

import com.chengshiun.springbootmall.dao.OrderDao;
import com.chengshiun.springbootmall.dao.ProductDao;
import com.chengshiun.springbootmall.dao.UserDao;
import com.chengshiun.springbootmall.dto.BuyItem;
import com.chengshiun.springbootmall.dto.CreateOrderRequest;
import com.chengshiun.springbootmall.dto.OrderQueryParams;
import com.chengshiun.springbootmall.model.Order;
import com.chengshiun.springbootmall.model.OrderItem;
import com.chengshiun.springbootmall.model.Product;
import com.chengshiun.springbootmall.model.User;
import com.chengshiun.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    //創建訂單 -> 新增 order table, order_item table
    //(使用@Transactional 避免其中一個 table 數據新增失敗)
    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        //檢查 user 是否存在(是有註冊過的)
        User user = userDao.getUserById(userId);

        if (user == null) {
            log.warn("該 user_id {} 尚未註冊", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //該筆訂單的總花費計算
        int totalAmount = 0;

        //訂單清單
        List<OrderItem> orderItemList = new ArrayList<>();

        //所有購買清單中 取出 每項購買商品數據
        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {

            //透過 productId 取得商品數據
            Product product = productDao.getProductById(buyItem.getProductId());

            //檢查 product 是否存在
            if (product == null) {
                log.warn("查無該 product_id {} 之商品，請另外挑選", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            //若存在 -> 檢查庫存數量是否足夠
            } else if (product.getStock() < buyItem.getQuantity()) {
                log.warn("該 product_id {} 之商品庫存不足，無法購買", product.getProductId());
                log.warn("庫存數量: {} / 欲購買數量: {}", product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            //通過檢查 -> 更新商品庫存量
            productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity());

            //商品花費 = 單價(商品數據 中可取得) * 數量(每項 購買商品數據 中可取得)
            int amount = product.getPrice() * buyItem.getQuantity();

            //totalAmount = 訂單總花費
            totalAmount = totalAmount + amount;

            //buyItem -> orderItem
            //將每項 購買商品數據 轉換為 訂單數據
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());   //請求參數中的購買數據
            orderItem.setQuantity(buyItem.getQuantity());     //請求參數中的購買數據
            orderItem.setAmount(amount);

            //每筆 訂單數據 存入 訂單清單中
            orderItemList.add(orderItem);
        }

        //創建一筆訂單 (取得該筆訂單的 orderId)
        Integer orderId = orderDao.insertOrder(userId, totalAmount);


        //同時也創建 訂單詳細記錄 (使用 userId 去關聯)
        orderDao.insertOrderItems(orderId, orderItemList);

        return orderId;
    }

    @Override
    public Order getFullOrderById(Integer orderId) {
        //取得訂單數據
        Order order = orderDao.getOrderById(orderId);

        //取得訂單清單數據
        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        //合併 訂單數據與 訂單清單數據
        order.setOrderItemList(orderItemList);

        return order;
    }

    @Override
    public List<Order> getOrdersByUser(OrderQueryParams orderQueryParams) {
        //先從所有訂單中 取得符合查詢條件的訂單
        List<Order> orderList = orderDao.getOrdersByUser(orderQueryParams);

        //把訂單中所有清單項目取出來放到 訂單中
        //迴圈跑完 則每一筆訂單中都會有 一個清單項目(orderItemList)
        for (Order order : orderList) {

            //getOrderItemsByOrderId() -> 找到同筆訂單中的所有清單項目
            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());

            order.setOrderItemList(orderItemList);
        }

        return orderList;
    }

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {

        return orderDao.countOrder(orderQueryParams);
    }
}
