package com.springDevelopers.Backend.Repositories;

import com.springDevelopers.Backend.Entities.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Integer> {
    Optional<CartItems> findByUserIdAndProductIdAndOrderId(Integer userId, Integer productId, Integer orderId);

}
