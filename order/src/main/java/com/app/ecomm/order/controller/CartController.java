package com.app.ecomm.order.controller;


import com.app.ecomm.order.Services.CartService;
import com.app.ecomm.order.dto.CartItemRequest;
import com.app.ecomm.order.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest cartItemRequest
    ){
        if(!cartService.addToCart(userId,cartItemRequest)){
            return ResponseEntity.badRequest().body("Product is not found or user is not found or Product is out of stock !!!");
        }
        return new ResponseEntity<>("Product is successfully added to the cart !!!",HttpStatus.ACCEPTED);
    }


    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String productId
    ){
        boolean deleted = cartService.deleteItemFromCart(userId,productId);
        return deleted ? ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getCartItems(
            @RequestHeader("X-User-ID") String userId
    ){
        List<CartResponse> res =  cartService.fetchCartItems(userId);
        if(res.size()>0){
            return new ResponseEntity<>(res,HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
