package com.appkings.co.ke.Cart;

public class CartModelClass {

    private String productId;
    private String uid;
    private String productName;
    private String sellingPrice;
    private String quantity;
    private String date;
    private String time;
    private String discount;

    public CartModelClass() {
        //empty constructor
    }

    public CartModelClass(String productId,
                          String uid,
                          String productName,
                          String sellingPrice,
                          String quantity,
                          String date,
                          String time,
                          String discount) {
        this.productId = productId;
        this.uid = uid;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.date = date;
        this.time = time;
        this.discount = discount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
