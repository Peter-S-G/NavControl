package com.example.petergeras.navcontrol.Model;

public class Product implements ListItem {

    private String productName;
    private String productUrl;
    private String productImageURL;
    private Double productPrice;

    private Integer position;

    private String companyName;


    public Product(String productName, String productImageURL, String productUrl, Double productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productUrl = productUrl;
        this.productImageURL = productImageURL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    @Override
    public String getTitle() {
        return productName;
    }

    @Override
    public String getSubtitle() {
        return "$" + productPrice;
    }

    @Override
    public String getImageURL() {
        return productImageURL;
    }

}
