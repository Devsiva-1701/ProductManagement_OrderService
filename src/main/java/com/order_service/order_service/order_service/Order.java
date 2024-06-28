package com.order_service.order_service.order_service;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String orderId;

    private Long totalCost;

    private Long no_of_products;

    private String cartId;

    private ArrayList<OrderProductDetails> productsMap;

    
}
