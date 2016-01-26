package com.sainsburys.coding.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class ProductItem {

    private String title;
    private String size;
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    @JsonProperty("description")
    private String description;

    private ProductItem(Builder builder) {
        setTitle(builder.title);
        setSize(builder.size);
        setUnitPrice(builder.unitPrice);
        setDescription(builder.description);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("title", title)
                .add("size", size)
                .add("unitPrice", unitPrice)
                .add("description", description)
                .toString();
    }

    public static final class Builder {
        private String title;
        private String size;
        private BigDecimal unitPrice;
        private String description;

        public Builder() {
        }

        public Builder withTitle(String val) {
            title = val;
            return this;
        }

        public Builder withSize(String val) {
            size = val;
            return this;
        }

        public Builder withUnitPrice(BigDecimal val) {
            unitPrice = val;
            return this;
        }

        public Builder withDescription(String val) {
            description = val;
            return this;
        }

        public ProductItem build() {
            return new ProductItem(this);
        }
    }
}
