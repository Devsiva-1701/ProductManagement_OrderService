package com.order_service.order_service.product;

public class Product {

    private String id;
    private String productName;
    private Long price;
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public Long getStock() {
        return stock;
    }
    
    public void setStock(Long stock) {
        this.stock = stock;
    }
    private Long stock;

    

    public Product(String id, String productName, Long price, Long stock) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String detailString() {
        return "Product [id=" + id + ", productName=" + productName + ", price=" + price + ", stock=" + stock + "]";
    }



    
    
}


