package com.springDevelopers.Backend.Entities;

import com.springDevelopers.Backend.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "orders_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String orderDescription;
    private Date orderDate;
    private String payment;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "order")
    private List<CartItems> cartItemsList;

}
