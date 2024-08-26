package com.chengshiun.springbootmall.util;

import java.util.List;

public class OrderPageUtil<T> {
    private Integer limit;
    private Integer offset;
    private Integer queryTotal;
    private List<T> result;

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
