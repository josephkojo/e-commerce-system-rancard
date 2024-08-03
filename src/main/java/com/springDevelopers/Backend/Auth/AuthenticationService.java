package com.springDevelopers.Backend.Auth;

import com.springDevelopers.Backend.Entities.Order;
import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Enums.OrderStatus;
import com.springDevelopers.Backend.Enums.Role;
import com.springDevelopers.Backend.Repositories.OrderRepository;
import com.springDevelopers.Backend.Repositories.UserRepository;
import com.springDevelopers.Backend.SpringSecurity.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrderRepository orderRepository;
    @CachePut(value = "users", key = "#registerRequest.email")
    public AuthenticateResponse registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setFirstname(registerRequest.getFirstname());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        Role role =  registerRequest.getRole().toLowerCase().equals(Role.CUSTOMER.toString().
                toLowerCase()) ? Role.CUSTOMER :Role.PRODUCT_OWNER;
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        this.userRepository.save(user);
        Order  order = new Order();
        order.setTotalAmount(BigDecimal.valueOf(0.00));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUser(user);
        orderRepository.save(order);

        String token = jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setId(user.getId());
        authenticateResponse.setFirstname(user.getFirstname());
        authenticateResponse.setEmail(user.getEmail());
        authenticateResponse.setRole(user.getRole().toString());
        authenticateResponse.setToken(token);
        return authenticateResponse;
    }




    @Cacheable(value = "users", key = "#authenticateRequest.email")
    public AuthenticateResponse loginUser(AuthenticateRequest authenticateRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateRequest.getEmail(), authenticateRequest.getPassword()));
        User user = this.userRepository.findByEmail(authenticateRequest.getEmail()).orElseThrow();
        String token = this.jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setId(user.getId());
        authenticateResponse.setFirstname(user.getFirstname());
        authenticateResponse.setEmail(user.getEmail());
        authenticateResponse.setRole(user.getRole().toString());
        authenticateResponse.setToken(token);
        return authenticateResponse;
    }





}
