package com.android45.doctorfromnature.models;

import java.io.Serializable;

public class MyFavoriteModel implements Serializable {
    private String productImg;
    private String productName;
    private String productPrice;
    private String description;
    private String documentID;

    public MyFavoriteModel() {

    }

    public MyFavoriteModel(String productImg, String productName, String productPrice) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public MyFavoriteModel(String productImg, String productName, String productPrice, String documentID) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.documentID = documentID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
