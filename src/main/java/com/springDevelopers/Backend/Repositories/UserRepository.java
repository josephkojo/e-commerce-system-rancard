package com.springDevelopers.Backend.Repositories;

import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
    @Repository
    public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByEmail(String email);
        boolean existsByRole(Role role);


}

