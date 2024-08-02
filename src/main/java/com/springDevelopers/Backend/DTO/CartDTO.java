package com.springDevelopers.Backend.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {
    private Integer Id;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Integer userId;

}
