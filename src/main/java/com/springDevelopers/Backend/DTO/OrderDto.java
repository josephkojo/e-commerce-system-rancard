package com.springDevelopers.Backend.DTO;

import com.springDevelopers.Backend.Enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDto {
    private Integer Id;
    private String orderDescription;
    private Date  orderDate;
    private String address;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private Integer userId;
}
