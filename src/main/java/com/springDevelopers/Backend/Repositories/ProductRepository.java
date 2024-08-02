package com.springDevelopers.Backend.Repositories;

import com.springDevelopers.Backend.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
