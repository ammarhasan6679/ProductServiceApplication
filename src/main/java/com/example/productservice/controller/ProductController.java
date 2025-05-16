package com.example.productservice.controller;

import com.example.productservice.dto.CreateProductRequestDTO;
import com.example.productservice.dto.UpdateProductrequestDTO;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.model.Product;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private ProductService service;

    //We can inject either fakeProductService or selfProductService in this constructor
    public ProductController(@Qualifier("selfProductService") ProductService inputService) {
        this.service = inputService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() throws ProductNotFoundException {
        List<Product> products=service.getAllProducts();
        if(products.size()==0 || products==null) {
            throw new ProductNotFoundException("Product is Empty!!!");
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") int id) throws ProductNotFoundException {
        //validations
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        Product product=service.getProductById(id);
        if(product==null) {
            throw new ProductNotFoundException("Product Not Found");
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products/{pageNo}/{pageSize}")
    public ResponseEntity<Page<Product>> getProductsByPage(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        //implementing pagination logic
        Page<Product> products=service.getPaginatedProducts(pageNo, pageSize);
        return ResponseEntity.ok(products);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequestDTO request) {
        if(request.getTitle()==null)
            throw new IllegalArgumentException("Title cannot be null");
        if(request.getDescription()==null)
            throw new IllegalArgumentException("Description cannot be null");
        if(request.getImageURL()==null)
            throw new IllegalArgumentException("ImageURL cannot be null");

        return ResponseEntity.ok(service.createProduct(
                request.getTitle(), request.getImageURL(), request.getCategory().getTitle(),request.getDescription()));
    }

    @PatchMapping("/products")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductrequestDTO request) throws ProductNotFoundException {
        if(request.getId()==null) throw new IllegalArgumentException("id is null");
        Product updatedProduct= service.updateProduct(request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Product> deleteProductById(@PathVariable("id") int id) throws ProductNotFoundException {
         if(id <= 0) {
             throw new IllegalArgumentException("Invalid id: " + id);
         }
         Product product=service.deleteProductById(id);
         if(product==null) {
             throw new ProductNotFoundException("Product Not Found!!!");
         }
         return ResponseEntity.ok(product);
    }
}
