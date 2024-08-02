package com.springDevelopers.Backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductToCartDTO {
    private Integer productId;
    private Integer userId;
    private Integer productQuantity;
}
