package com.springDevelopers.Backend.DTO;

import lombok.Data;

@Data
public class PlaceOrderDto {
    private Integer userId;
    private String address;
    private String orderDescription;

}
