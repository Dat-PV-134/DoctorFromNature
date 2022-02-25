package com.android45.doctorfromnature.models;

import java.io.Serializable;

public class DeliverModel implements Serializable {
    private String productsName;
    private String productsPrice;
    private String productsQuantity;
    private String productImg;
    private String totalPrice;
    private String totalProductsPrice;
    private String customerName;
    private String customerPhoneNumber;
    private String customerAddress;
    private String checkDeliver;
    private String documentID;

    public DeliverModel() {

    }

    public DeliverModel(String productsName, String productsPrice, String productsQuantity, String productImg, String totalPrice, String totalProductsPrice, String customerName, String customerPhoneNumber, String customerAddress, String checkDeliver, String documentID) {
        this.productsName = productsName;
        this.productsPrice = productsPrice;
        this.productsQuantity = productsQuantity;
        this.productImg = productImg;
        this.totalPrice = totalPrice;
        this.totalProductsPrice = totalProductsPrice;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerAddress = customerAddress;
        this.checkDeliver = checkDeliver;
        this.documentID = documentID;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getProductsPrice() {
        return productsPrice;
    }

    public void setProductsPrice(String productsPrice) {
        this.productsPrice = productsPrice;
    }

    public String getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(String productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalProductsPrice() {
        return totalProductsPrice;
    }

    public void setTotalProductsPrice(String totalProductsPrice) {
        this.totalProductsPrice = totalProductsPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCheckDeliver() {
        return checkDeliver;
    }

    public void setCheckDeliver(String checkDeliver) {
        this.checkDeliver = checkDeliver;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
