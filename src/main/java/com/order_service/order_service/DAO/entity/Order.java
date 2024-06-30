package com.order_service.order_service.DAO.entity;

import java.util.List;

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

    private List<OrderProductDetails> productsMap;

    
}
