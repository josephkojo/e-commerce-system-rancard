package com.springDevelopers.Backend.Services;

import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Repositories.ProductRepository;
import com.springDevelopers.Backend.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // You might need to initialize other components or configurations
    }

    @Test
    public void testAddProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setUserId(1);
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(BigDecimal.valueOf(100.0));
        productDTO.setQuantity(10);

        User user = new User();
        user.setId(1);

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setOwner(user);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.addProduct(productDTO);

        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getDescription(), result.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setUserId(1);
        productDTO.setName("Updated Product");
        productDTO.setDescription("Updated Description");
        productDTO.setPrice(BigDecimal.valueOf(150.0));
        productDTO.setQuantity(20);

        User user = new User();
        user.setId(1);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Product");

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(existingProduct));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Optional<Product> result = productService.updateProduct(1, productDTO);

        assertTrue(result.isPresent());
        assertEquals(productDTO.getName(), result.get().getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testGetAllProducts() {
        Product product = new Product();
        product.setName("Test Product");

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProduct();

        assertEquals(1, result.size());
        assertEquals(product.getName(), result.get(0).getName());
    }

    @Test
    public void testDeleteProduct() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));

        boolean result = productService.deleteProduct(1);

        assertTrue(result);
        verify(productRepository, times(1)).deleteById(anyInt());
    }
    

}
