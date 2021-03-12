package com.smartwareafrica.pos.Products;

public class ProductsModelClass {

    private String buyingPrice;

    private String productDescription;
    private String productName;
    private String sellingPrice;
    private String userId;

    public ProductsModelClass() {
        //Empty constructor

    }

    public ProductsModelClass(String buyingPrice,
                              String productDescription,
                              String productName,
                              String sellingPrice,
                              String userId) {
        this.buyingPrice = buyingPrice;
        this.productDescription = productDescription;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.userId = userId;
    }

    public String getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(String buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
