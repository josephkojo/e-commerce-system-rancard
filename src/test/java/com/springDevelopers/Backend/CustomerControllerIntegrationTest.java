package com.springDevelopers.Backend.Controllers;

import com.springDevelopers.Backend.DTO.AddProductToCartDTO;
import com.springDevelopers.Backend.DTO.OrderDto;
import com.springDevelopers.Backend.DTO.PlaceOrderDto;
import com.springDevelopers.Backend.Services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testAddProductToCart() {
        AddProductToCartDTO dto = new AddProductToCartDTO();
        ResponseEntity<String> response = restTemplate.postForEntity("/customer/addCart", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPlaceOrder() {
        PlaceOrderDto dto = new PlaceOrderDto();
        ResponseEntity<OrderDto> response = restTemplate.postForEntity("/customer/placeOrder", dto, OrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }
}
