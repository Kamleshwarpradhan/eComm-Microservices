package com.app.ecomm.product.Services;


import com.app.ecomm.product.Model.Product;
import com.app.ecomm.product.Repository.ProductRepository;
import com.app.ecomm.product.dto.ProductRequest;
import com.app.ecomm.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest){
        Product product = new Product();

        updateProductFromProductrequest(product,productRequest);

        Product savedProduct = productRepository.save(product);

        return mapProducttoProductResponse(savedProduct);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFromProductrequest(existingProduct,productRequest);
                    Product savedProduct = productRepository.save(existingProduct);
                    return mapProducttoProductResponse(savedProduct);
                });
    }

    private ProductResponse mapProducttoProductResponse(Product savedProduct) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setId(savedProduct.getId());
        productResponse.setProductName(savedProduct.getProductName());
        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setCategory(savedProduct.getCategory());
        productResponse.setPrice(savedProduct.getPrice());
        productResponse.setStockQuantity(savedProduct.getStockQuantity());
        productResponse.setImageUrl(savedProduct.getImageUrl());
        productResponse.setActive(savedProduct.getActive());
        productResponse.setId(savedProduct.getId());
        return productResponse;
    }

    private void updateProductFromProductrequest(Product product, ProductRequest productRequest) {
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
    }


    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapProducttoProductResponse)
                .collect(Collectors.toList());
    }

    public Boolean deleteProduct(Long id) {
       return productRepository.findById(id)
               .map(product -> {
                   product.setActive(false);
                   productRepository.save(product);
                   return true;
               }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapProducttoProductResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductByIdAndActiveTrue(String id) {
        return productRepository.findByIdAndActiveTrue(Long.valueOf(id))
                .map(this::mapProducttoProductResponse);
    }
}
