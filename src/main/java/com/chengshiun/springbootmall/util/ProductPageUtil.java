package com.chengshiun.springbootmall.util;

import java.util.List;

//泛型
public class ProductPageUtil<T> {
    private Integer limit;
    private Integer offset;
    private Integer queryTotal;   //查詢條件下的商品總數量
    private List<T> result;       //商品數據

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getQueryTotal() {
        return queryTotal;
    }

    public void setQueryTotal(Integer queryTotal) {
        this.queryTotal = queryTotal;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
