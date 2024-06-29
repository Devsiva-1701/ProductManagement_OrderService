package com.order_service.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String productName;
    private Long price;
    private Long stock;
    private String sellerName;
    private Long sellerId;
    private String categoryName;
    private Long categoryId;
    
}
