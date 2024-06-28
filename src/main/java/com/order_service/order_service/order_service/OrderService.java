package com.order_service.order_service.order_service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order_service.order_service.cart.CartEmptyException;
import com.order_service.order_service.order_repo.CartOrderRepository;
import com.order_service.order_service.product.Product;

@Service
public class OrderService {
    private String cartID = "C9812R";

    HashMap<String , Long> customerCart = new HashMap<>();
    // ArrayList<CartProduct> cartProds = new ArrayList<>();

    @Autowired
    CartOrderRepository cartOrderRepo;

    @Autowired
    RestTemplate restTemplate;

    public String addProdcutToCart(String prodID , Long quantity)
    {
        HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.put("Authorization", new ArrayList<>());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ArrayList<Product>> responseType = new ParameterizedTypeReference<ArrayList<Product>>() {};

            ResponseEntity<ArrayList<Product>> responseEntity = restTemplate.exchange("http://localhost:8080/products/getProducts", HttpMethod.GET, entity, responseType);

            ArrayList<Product> products = responseEntity.getBody();

            if(!products.isEmpty())
            {

                products.stream().forEach( product ->{

                    if(String.valueOf(product.getId()).equals(prodID))
                    {
                        customerCart.put(prodID, customerCart.getOrDefault(prodID , 0l)+quantity);

                    }
                

                }
                );
                
                return "Product added to the cart successfully...\n"+customerCart;
            }
            else
            {
                return "No products are in the store...";
            }

    }

    public ResponseEntity<Object> buyCartService()
    {
            Long totalCost = 0l;
            Long totalCount = 0l;
            
            try {
                if( customerCart.isEmpty() )
                {
                    throw new CartEmptyException("The Cart is Empty");
                    // return ResponseEntity.ok().body("The Cart Is Empty");
                }
                
            } catch (CartEmptyException cartEmptyException) {
                System.err.println(cartEmptyException);
            }

            

            Iterator<Map.Entry<String, Long>> iterator = customerCart.entrySet().iterator();
            

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.put("Authorization", new ArrayList<>());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ArrayList<Product>> responseType = new ParameterizedTypeReference<ArrayList<Product>>() {};

            ResponseEntity<ArrayList<Product>> responseEntity = restTemplate.exchange("http://localhost:8080/products/getProducts", HttpMethod.GET, entity, responseType);

            System.out.println(responseEntity.getBody());

            ArrayList<Product> products = responseEntity.getBody();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm-ss-SS");
            String currentTime = formatter.format(LocalTime.now());
            String orderID = "O"+currentTime+"R";
            ArrayList<OrderProductDetails> productDetailsList = new ArrayList<>();

            if(products.isEmpty())
            {
                return ResponseEntity.ok().body(("No products found in the store...")  );
            }
            else
            {
                while(iterator.hasNext())
            {
                Map.Entry<String, Long> cartEntry = iterator.next();
                for( Product product : products )
                {
                    if(product.getId().equals(cartEntry.getKey()))
                    {
                        if(product.getStock() >= cartEntry.getValue())
                        {
                        
                            productDetailsList.add( new OrderProductDetails(product.getId(),product.getProductName() , (product.getStock() - cartEntry.getValue()) ) );
                            product.setStock(cartEntry.getValue() );
                            totalCost += product.getPrice() * cartEntry.getValue();
                            totalCount ++;
                            iterator.remove();
                        }
                        
                    }
                }

            }

            Order order = new Order();

            order.setCartId(cartID);
            order.setNo_of_products(totalCount);
            order.setOrderId(orderID);
            order.setTotalCost(totalCost);
            order.setProductsMap(productDetailsList);

            cartOrderRepo.save(order);

            HttpHeaders headers_2 = new HttpHeaders();

            headers_2.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<Product>> requestEntity = new HttpEntity<List<Product>>(products , headers_2);

            ResponseEntity<String> response_entity = restTemplate.exchange("http://localhost:8080/products/updatePurchase", HttpMethod.PUT , requestEntity ,String.class);
            
            String response = response_entity.getBody();

            System.out.println(products);
            System.out.println(response);
            return ResponseEntity.ok().body( "Purchase Successful..." );
            }

            
    }

    public ArrayList<Order>  showPurchaseHistory()
    {
        return (ArrayList<Order>) cartOrderRepo.findByCartId(cartID);
    }
    
}
