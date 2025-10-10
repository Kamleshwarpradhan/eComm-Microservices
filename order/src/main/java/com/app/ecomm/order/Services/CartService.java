package com.app.ecomm.order.Services;


import com.app.ecomm.order.Model.CartItem;
import com.app.ecomm.order.Repository.CartItemRepository;
import com.app.ecomm.order.client.ProductServiceClient;
import com.app.ecomm.order.client.UserServiceClient;
import com.app.ecomm.order.dto.CartItemRequest;
import com.app.ecomm.order.dto.CartResponse;
import com.app.ecomm.order.dto.ProductResponse;
import com.app.ecomm.order.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    int attempt = 0;
//    @CircuitBreaker(name = "productService",fallbackMethod = "addToCartFallback")
    @Retry(name = "retryBreaker",fallbackMethod = "addToCartFallback")
    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {
        System.out.println("ATTEMPT NO: " + ++attempt);
        // Validating Product (is it actually exist or not)
        ProductResponse productResponse = productServiceClient.getProductdetails(cartItemRequest.getProductId());

        if(productResponse == null || productResponse.getStockQuantity()<cartItemRequest.getQuantity())
            return false;

        // Validating User(is it actually exist or not)
          UserResponse userResponse = userServiceClient.getUserdetails(userId);
          if(userResponse==null){
              return false;
          }
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//
//        if(userOpt.isEmpty())
//            return false;
//
//        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId,cartItemRequest.getProductId());

        if(existingCartItem != null){
            // Product is already present in Cart so just increase the quantity
            BigDecimal perUnitPrice = existingCartItem.getPrice().divide(BigDecimal.valueOf(existingCartItem.getQuantity()));

            existingCartItem.setQuantity(existingCartItem.getQuantity()+cartItemRequest.getQuantity());
            existingCartItem.setPrice(perUnitPrice.multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));


            cartItemRepository.save(existingCartItem);
        }else{
            // Product is not present in the cart so we have to add it
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartItem.getProductId());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItem.setQuantity(cartItemRequest.getQuantity());

            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public boolean addToCartFallback(String userId, CartItemRequest cartItemRequest,Exception exception){
        System.out.println("FALLBACK CALLED with "+ exception);
        exception.printStackTrace();
        return false;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartResponse> fetchCartItems(String userId) {
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartItemRepository.findByUserId(userId).stream()
                .map(this::mapCartItemtoCartResponse)
                .collect(Collectors.toList());
    }

    private CartResponse mapCartItemtoCartResponse(CartItem cartItem) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cartItem.getId());
        cartResponse.setProductId(cartItem.getProductId());
        cartResponse.setPrice(cartItem.getPrice());
        cartResponse.setQuantity(cartItem.getQuantity());

        return cartResponse;
    }

    public void clearCart(String userId) {
//        Optional<User> user = userRepository.findById(Long.valueOf(userId));
//
//        if(user.isPresent()){
//            cartItemRepository.deleteByUser(user.get());
//        }
        cartItemRepository.deleteByUserId(userId);
    }
}
