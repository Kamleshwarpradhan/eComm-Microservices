package com.app.ecomm.order.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartResponse {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
