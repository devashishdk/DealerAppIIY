package com.devashish.erpapp;

public class Order {
    String dealer_id;
    String product_id;

    public Order(String dealer_id, String product_id, String product_image, String product_name, String product_price, String quantity, String status) {
        this.dealer_id = dealer_id;
        this.product_id = product_id;
        this.product_image = product_image;
        this.product_name = product_name;
        this.product_price = product_price;
        this.quantity = quantity;
        this.status = status;
    }

    public Order() {

    }

    public String getDealer_id() {

        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String product_image;
    String product_name;
    String product_price;
    String quantity;
    String status;

}
