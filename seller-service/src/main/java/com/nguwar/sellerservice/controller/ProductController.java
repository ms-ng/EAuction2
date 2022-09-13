package com.nguwar.sellerservice.controller;

import com.nguwar.sellerservice.dto.NewProductDTO;
import com.nguwar.sellerservice.dto.ProductDTO;
import com.nguwar.sellerservice.dto.ProductViewDTO;
import com.nguwar.sellerservice.dto.mapper.NewProductMapper;
import com.nguwar.sellerservice.dto.mapper.ProductMapper;
import com.nguwar.sellerservice.exception.ProductCreationException;
import com.nguwar.sellerservice.exception.ProductDeletionException;
import com.nguwar.sellerservice.model.Product;
import com.nguwar.sellerservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    @PostMapping("/e-auction/api/v1/seller/add-product")
    public ResponseEntity<String> addProduct(@Valid @RequestBody NewProductDTO newProductDTO){

        try {
            Product product = NewProductMapper.INSTANCE.newProductDTOToProduct(newProductDTO);
            Product createdProduct = productService.addProduct(product);
            return ResponseEntity.ok(String.format("New product added successfully!"));
        }
        catch(ProductCreationException ex) {
            logger.error("result --> {} " , ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping ("/e-auction/api/v1/seller/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId){

        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok("product deleted successfully!");

        } catch(ProductDeletionException ex) {
            logger.error("result --> {} " , ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    @GetMapping ("/e-auction/api/v1/seller/product/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId){

        Optional<Product> product = productService.getProduct(productId);

        if(product.isPresent()) {
            ProductDTO productDTO = ProductMapper.INSTANCE.productToProductDTO(product.get());
            return ResponseEntity.ok().body(productDTO);
        } else {
            logger.error("result --> Product Not Found with Id : {} " , productId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping ("/e-auction/api/v1/seller/product")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "shortName") String sortColumn){

        Page<Product> products = productService.getProducts(pageNumber, pageSize, sortColumn);

        List<ProductDTO> productDTOs = products.getContent().stream()
                .map(p ->ProductMapper.INSTANCE.productToProductDTO(p))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(productDTOs);
    }

    @GetMapping("/e-auction/api/v1/seller/show-bids/{productId}")
    public ProductViewDTO getProductBids(@PathVariable long productId){
        return productService.getProductBiddings(productId);
    }
}
