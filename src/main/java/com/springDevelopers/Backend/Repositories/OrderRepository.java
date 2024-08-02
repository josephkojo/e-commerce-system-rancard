package com.springDevelopers.Backend.Repositories;

import com.springDevelopers.Backend.Entities.Order;
import com.springDevelopers.Backend.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByUserIdAndOrderStatus(Integer userId, OrderStatus orderStatus);


}
