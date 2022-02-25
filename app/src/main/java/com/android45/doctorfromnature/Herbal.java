package com.android45.doctorfromnature;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Herbal implements Serializable {
//    @SerializedName("id")phamv
//    @Expose
    private String id;
//    @SerializedName("img")
//    @Expose
    private String img;
//    @SerializedName("name")
//    @Expose
    private String name;
//    @SerializedName("price")
//    @Expose
    private String price;
//    @SerializedName("description")
//    @Expose
    private String description;
//    @SerializedName("status")
//    @Expose
    private String amount;

    private String documentID;

    private int isFavorite = 0;

    public Herbal() {
    }

    public Herbal(String id, String img, String name, String price, String description, String amount) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.price = price;
        this.description = description;
        this.amount = amount;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
