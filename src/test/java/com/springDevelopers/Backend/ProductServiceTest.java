package com.springDevelopers.Backend;


import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Repositories.ProductRepository;
import com.springDevelopers.Backend.Repositories.UserRepository;
import com.springDevelopers.Backend.Services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(BigDecimal.valueOf(100));
        productDTO.setUserId(1);

        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100));
        product.setOwner(user);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Correct method call to addProduct
        Product createdProduct = productService.addProduct(productDTO);

        // Assertions
        assertNotNull(createdProduct);
        assertEquals(productDTO.getName(), createdProduct.getName());
        assertEquals(productDTO.getDescription(), createdProduct.getDescription());
        assertEquals(productDTO.getPrice(), createdProduct.getPrice());
        assertEquals(productDTO.getUserId(), createdProduct.getOwner().getId());

        // Verifications
        verify(productRepository, times(1)).save(any(Product.class));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setDescription("Updated Description");
        productDTO.setPrice(BigDecimal.valueOf(200));
        productDTO.setUserId(1);

        User user = new User();
        user.setId(1);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Test Product");
        existingProduct.setDescription("Test Description");
        existingProduct.setPrice(BigDecimal.valueOf(100));
        existingProduct.setOwner(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Optional<Product> updatedProduct = productService.updateProduct(1, productDTO);

        assertTrue(updatedProduct.isPresent());
        assertEquals(productDTO.getName(), updatedProduct.get().getName());
        assertEquals(productDTO.getDescription(), updatedProduct.get().getDescription());
        assertEquals(productDTO.getPrice(), updatedProduct.get().getPrice());
        assertEquals(productDTO.getUserId(), updatedProduct.get().getOwner().getId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllProduct() {
        User user = new User();
        user.setId(1);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(BigDecimal.valueOf(100));
        product1.setOwner(user);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(BigDecimal.valueOf(200));
        product2.setOwner(user);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> productDTOList = productService.getAllProduct();

        assertEquals(2, productDTOList.size());
        assertEquals("Product 1", productDTOList.get(0).getName());
        assertEquals("Product 2", productDTOList.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Boolean result = productService.deleteProduct(1);

        assertTrue(result);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        Boolean result = productService.deleteProduct(1);

        assertFalse(result);
        verify(productRepository, never()).deleteById(1);
    }

    @Test
    void testAddProductEmitter() {
        SseEmitter emitter = productService.addProductEmitter();

        assertNotNull(emitter);
        assertTrue(productService.emitters.contains(emitter));
    }

    @Test
    void testNotifyProductUpdate() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        SseEmitter emitter = mock(SseEmitter.class);
        productService.emitters.add(emitter);

        productService.notifyProductUpdate(product);

        verify(emitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }
}
