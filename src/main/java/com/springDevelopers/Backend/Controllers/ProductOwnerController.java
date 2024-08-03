package com.springDevelopers.Backend.Controllers;

import com.springDevelopers.Backend.DTO.InsightsDto;
import com.springDevelopers.Backend.DTO.ProductDTO;
import com.springDevelopers.Backend.Entities.Product;
import com.springDevelopers.Backend.Services.CustomerService;
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
@RequestMapping("/products")
public class ProductOwnerController {
    private final ProductService productService;
    private final CustomerService customerService;
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO){
        Product product = this.productService.addProduct(productDTO);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {
        Optional<Product> updatedProduct = productService.updateProduct(id, productDTO);
        return updatedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProduct(){
        List<ProductDTO> productsList = this.productService.getAllProduct();
        return ResponseEntity.ok(productsList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Integer id){
        if(this.productService.deleteProduct(id)){
            return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }



    @GetMapping("/insights")
    public ResponseEntity<InsightsDto> productInsights(){
        if(this.customerService.insights() != null){
            return new ResponseEntity<>(this.customerService.insights(), HttpStatus.OK);
        }
        return ResponseEntity.status(404).body(null);
    }


    @GetMapping("/stream")
    public SseEmitter streamProducts() {
        return productService.addProductEmitter();
    }

    public void sendProductUpdate(Product product) {
        productService.notifyProductUpdate(product);
    }
}
