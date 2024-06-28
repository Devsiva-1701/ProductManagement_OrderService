package com.order_service.order_service.cart;

public class CartEmptyException extends Exception {

    public CartEmptyException()
    {
        super();
    }

    public CartEmptyException(String message)
    {
        super(message);
    }
    
}
