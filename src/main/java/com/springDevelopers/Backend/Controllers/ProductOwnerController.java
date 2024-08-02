package com.springDevelopers.Backend.Controllers;

import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/productOwner")
public class ProductOwnerController {
    private final ProductService productService;
    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO){
        Product product = this.productService.addProduct(productDTO);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }



    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId, @RequestBody ProductDTO productDTO) {
        Optional<Product> updatedProduct = productService.updateProduct(productId, productDTO);
        return updatedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/allProduct")
    public ResponseEntity<List<ProductDTO>> getAllProduct(){
        List<ProductDTO> productsList = this.productService.getAllProduct();
        return ResponseEntity.ok(productsList);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Integer productId){
        if(this.productService.deleteProduct(productId)){
            return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }


    @GetMapping("/stream")
    public SseEmitter streamProducts() {
        return productService.addProductEmitter();
    }

    public void sendProductUpdate(Product product) {
        productService.notifyProductUpdate(product);
    }
}
