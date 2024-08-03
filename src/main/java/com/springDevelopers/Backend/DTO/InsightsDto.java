package com.springDevelopers.Backend.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsightsDto {
    private Integer numberOfOrders;
    private OrderDto highestOrderMade;
    private Integer numberOfProduct ;
}
