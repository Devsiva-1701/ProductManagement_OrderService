package com.order_service.order_service.DAO.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.order_service.order_service.DAO.entity.Order;

import java.util.ArrayList;


@Repository
public interface CartOrderRepository extends MongoRepository<Order , String> {

    @Query("{'cartId': ?0}")
    ArrayList<Order> findByCartId(String cartId);
    
}
