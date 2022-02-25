package com.android45.doctorfromnature.models;

public class DeliverItemModel {
    private String productImg;
    private String productName;
    private String productPrice;
    private String productQuantity;

    public DeliverItemModel() {

    }

    public DeliverItemModel(String productImg, String productName, String productPrice, String productQuantity) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
}
