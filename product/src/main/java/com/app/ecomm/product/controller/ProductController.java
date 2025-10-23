package com.app.ecomm.product.controller;


import com.app.ecomm.product.Services.ProductService;
import com.app.ecomm.product.dto.ProductRequest;
import com.app.ecomm.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    // productService is a spring managed entity so we have to use
    // a constructor to link this entity
    // or use @Autowired configuration
    // or use option like @RequiredArgConstructor which eventually create the constructor
    public final ProductService productService;

    // Only for simulation purpose
    @GetMapping("/simulate")
    public ResponseEntity<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail){
        if(fail){
            throw new RuntimeException("Simulated Failure For Testing");
        }
        return ResponseEntity.ok("Product Service is OK");
    }


    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id){
        return productService.getProductByIdAndActiveTrue(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,@RequestBody ProductRequest productRequest){
        return productService.updateProduct(id,productRequest)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        Boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword){
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
