package com.order_service.order_service.rest_controllers;

import org.springframework.web.bind.annotation.RestController;

import com.order_service.order_service.DAO.entity.Order;
import com.order_service.order_service.service.OrderService;

import java.util.ArrayList;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/addToCart/{prodID}")
    public String addProductToCart( @PathVariable Long prodID , @RequestParam Long quantity ) {

        return orderService.addProdcutToCart(prodID, quantity);
        
    }

    @GetMapping("/buyCart")
    public Object buyCartProducts() {

        return orderService.buyCartService();
    }

    @GetMapping("/purchaseHistory")
    public ArrayList<Order> getHistroy(@RequestBody String cartID) {

        return orderService.showPurchaseHistory();

    }
        
        
        

    }
        


