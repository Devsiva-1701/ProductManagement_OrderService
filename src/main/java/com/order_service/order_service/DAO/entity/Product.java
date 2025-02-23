package com.order_service.order_service.DAO.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private Long id;
    private String productName;
    private Long price;
    private Long stock;
    private Long sellerId;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    
}


