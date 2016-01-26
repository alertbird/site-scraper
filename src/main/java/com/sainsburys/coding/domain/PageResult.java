package com.sainsburys.coding.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.google.common.base.MoreObjects;

public class PageResult {
    private List<ProductItem> results;
    private BigDecimal total;

    public PageResult(List<ProductItem> results, BigDecimal total) {
        this.results = results;
        this.total = total;
    }

    public List<ProductItem> getResults() {
        return results;
    }

    public void setResults(List<ProductItem> results) {
        this.results = results;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("results", results)
                .add("total", total)
                .toString();
    }
}
