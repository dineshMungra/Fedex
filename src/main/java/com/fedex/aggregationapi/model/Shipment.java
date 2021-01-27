package com.fedex.aggregationapi.model;

import java.util.List;

/**
 * Models the result of a query to the Shipment API.
 * A shipment consists of an order number and a list of products.
 */
public class Shipment {
    private long orderNumber;
    private List<Product> productList;

    public Shipment() {}

    public Shipment(long orderNumber, List<Product> products) {
        this.orderNumber = orderNumber;
        this.productList = products;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
