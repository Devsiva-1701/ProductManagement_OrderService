package com.order_service.order_service.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order_service.order_service.DAO.entity.Order;
import com.order_service.order_service.DAO.entity.OrderProductDetails;
import com.order_service.order_service.DAO.entity.Product;
import com.order_service.order_service.DAO.repository.CartOrderRepository;
import com.order_service.order_service.DTO.ProductDTO;
import com.order_service.order_service.exceptions.CartEmptyException;

@Service
public class OrderService {
    private String cartID = "C9812R";

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    HashMap<Long , Long> customerCart = new HashMap<>();

    @Autowired
    CartOrderRepository cartOrderRepo;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    KafkaTemplate<String , Order> kafkaTemplate;

    public String addProdcutToCart(Long prodID , Long quantity)
    {
        HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.put("Authorization", new ArrayList<>());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ArrayList<ProductDTO>> responseType = new ParameterizedTypeReference<ArrayList<ProductDTO>>() {};

            ResponseEntity<ArrayList<ProductDTO>> responseEntity = restTemplate.exchange("http://localhost:8080/products/getProductsBuyers", HttpMethod.GET, entity, responseType);

            ArrayList<ProductDTO> productsDTO = responseEntity.getBody();

            System.out.println(productsDTO);

            if(!productsDTO.isEmpty())
            {

                productsDTO.stream().forEach( product ->{

                    if(product.getId() == prodID)
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

    public String buyCartService()
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

            

            Iterator<Map.Entry<Long, Long>> iterator = customerCart.entrySet().iterator();
            

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.put("Authorization", new ArrayList<>());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ArrayList<Product>> responseType = new ParameterizedTypeReference<ArrayList<Product>>() {};

            ResponseEntity<ArrayList<Product>> responseEntity = restTemplate.exchange("http://localhost:8080/products/getProductsBuyers", HttpMethod.GET, entity, responseType);

            System.out.println(responseEntity.getBody());

            ArrayList<Product> products = responseEntity.getBody();
            ArrayList<Product> buyProductsList =  new ArrayList<>();
            

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm-ss-SS");
            String currentTime = formatter.format(LocalTime.now());
            String orderID = "O"+currentTime+"R";
            ArrayList<OrderProductDetails> productDetailsList = new ArrayList<>();

            if(products.isEmpty())
            {
                return "No Product Found...";
            }
            else
            {
                while(iterator.hasNext())
            {
                Map.Entry<Long, Long> cartEntry = iterator.next();
                for( Product product : products )
                {
                    if(product.getId() == cartEntry.getKey())
                    {
                        if(product.getStock() >= cartEntry.getValue())
                        {
                        
                            productDetailsList.add( new OrderProductDetails( orderID , product.getId() , product.getProductName() , cartEntry.getValue() ) );
                            product.setStock( product.getStock() - cartEntry.getValue() );
                            buyProductsList.add(product);
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

            HttpEntity<List<Product>> requestEntity = new HttpEntity<List<Product>>(buyProductsList , headers_2);

            ResponseEntity<String> response_entity = restTemplate.exchange("http://localhost:8080/products/updatePurchase", HttpMethod.PUT , requestEntity ,String.class);
            
            String response = response_entity.getBody();

            // System.out.println(products);
            // System.out.println(response);
            // return "Purchase Successfull...";

            System.out.println(order.getProductsMap());

            sendOrderDetails( topic , order);

            return response;

            }

            
    }

    public ArrayList<Order>  showPurchaseHistory()
    {
        return (ArrayList<Order>) cartOrderRepo.findByCartId(cartID);
    }

    public void sendOrderDetails( String topic , Order order )
    {
        kafkaTemplate.send( topic , order );   
        System.out.println("Message sent...");    
    }
    
}
