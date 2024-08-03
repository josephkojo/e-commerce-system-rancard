package com.springDevelopers.Backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductOwnerController.class)
public class ProductOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(BigDecimal.valueOf(100.0));
        productDTO.setQuantity(10);
        productDTO.setUserId(1);

        Product product = new Product();
        product.setId(1L);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        when(productService.addProduct(any(ProductDTO.class))).thenReturn(product);

        mockMvc.perform(post("/productOwner/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.description").value(productDTO.getDescription()));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setDescription("Updated Description");
        productDTO.setPrice(BigDecimal.valueOf(150.0));
        productDTO.setQuantity(20);
        productDTO.setUserId(1);

        Product product = new Product();
        product.setId(1L);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        when(productService.updateProduct(anyInt(), any(ProductDTO.class))).thenReturn(Optional.of(product));

        mockMvc.perform(put("/productOwner/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.description").value(productDTO.getDescription()));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");

        when(productService.getAllProduct()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/productOwner/allProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(productDTO.getName()));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(anyInt())).thenReturn(true);

        mockMvc.perform(delete("/productOwner/deleteProduct/1"))
                .andExpect(status().isNoContent());
    }
}
