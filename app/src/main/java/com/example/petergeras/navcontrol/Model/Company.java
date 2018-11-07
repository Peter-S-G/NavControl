package com.example.petergeras.navcontrol.Model;

public class Company implements ListItem {

    private String name;
    private String stockSymbol;
    private String logoUrl;
    private Double stockPrice;

    private Integer position;


    public Company(String name, String stockSymbol, String logoUrl, Double stockPrice) {
        this.name = name;
        this.stockSymbol = stockSymbol;
        this.logoUrl = logoUrl;
        this.stockPrice = stockPrice;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return name + " (" + stockSymbol + ")";
    }

    // Round the stock price up or down
    @Override
    public String getSubtitle() {
        double roundOff = Math.round(stockPrice * 100.0) / 100.0;
        return "$" + roundOff;
    }

    @Override
    public String getImageURL() {
        return logoUrl;
    }
}
