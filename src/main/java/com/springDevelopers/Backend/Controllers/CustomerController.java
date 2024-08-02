package com.springDevelopers.Backend.Controllers;

import com.springDevelopers.Backend.DTO.AddProductToCartDTO;
import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/addCart")
    public ResponseEntity<?> addProductToCart(@RequestBody AddProductToCartDTO addProductToCartDTO){
        return  this.customerService.addProductToCart(addProductToCartDTO);
    }

}
