package com.chengshiun.springbootmall.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateOrderRequest {
    @NotBlank
    private List<BuyItem> buyItemList;

    public List<BuyItem> getBuyItemList() {
        return buyItemList;
    }

    public void setBuyItemList(List<BuyItem> buyItemList) {
        this.buyItemList = buyItemList;
    }
}
