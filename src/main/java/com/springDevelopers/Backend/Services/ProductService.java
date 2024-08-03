package com.springDevelopers.Backend.Services;

import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Repositories.ProductRepository;
import com.springDevelopers.Backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"products"})
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @CachePut(value = "products", key = "#productDTO.getUserId() + '_' + #productDTO.getName()")
    public Product addProduct(ProductDTO productDTO) {
        User productOwner = userRepository.findById(productDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setOwner(productOwner);
        productRepository.save(product);
        notifyProductUpdate(product);
        return product;
    }

    @CachePut(value = "products", key = "#productId")
    public Optional<Product> updateProduct(Integer productId, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(productId);
        User productOwner = userRepository.findById(productDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return productOptional.map(product -> {
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setOwner(productOwner);
            productRepository.save(product);
            notifyProductUpdate(product);
            return product;
        });
    }

    @Cacheable(value = "products")
    public List<ProductDTO> getAllProduct() {
        List<Product> allProducts = this.productRepository.findAll();
        return allProducts.stream().map(this::convertToProductDto)
                .collect(Collectors.toList());
    }

    public SseEmitter addProductEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void notifyProductUpdate(Product product) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("product-update").data(product));
            } catch (IOException e) {
                emitter.complete();
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    private ProductDTO convertToProductDto(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setUpdatedAt(product.getUpdatedAt());
        productDTO.setUserId(product.getOwner().getId());
        return productDTO;
    }

    @CacheEvict(value = "products", key = "#productId")
    public Boolean deleteProduct(Integer productId) {
        Optional<Product> product = this.productRepository.findById(productId);
        Boolean productIsPresent = product.isPresent();
        if (productIsPresent) {
            this.productRepository.deleteById(productId);
            return true;
        }
        return false;
    }
}
