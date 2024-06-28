package com.order_service.order_service.order_service;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDetails {

    private String productID;

    private String productName;

    private Long quantity;
    
    
    
}
