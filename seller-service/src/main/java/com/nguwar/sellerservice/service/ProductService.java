package com.nguwar.sellerservice.service;

import com.nguwar.sellerservice.dto.ProductDTO;
import com.nguwar.sellerservice.dto.ProductViewDTO;
import com.nguwar.sellerservice.dto.mapper.ProductMapper;
import com.nguwar.sellerservice.exception.ProductCreationException;
import com.nguwar.sellerservice.exception.ProductDeletionException;
import com.nguwar.sellerservice.model.Product;
import com.nguwar.sellerservice.model.ProductBid;
import com.nguwar.sellerservice.repository.ProductBidRepository;
import com.nguwar.sellerservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductBidRepository productBidRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductBidRepository productBidRepository) {
        this.productRepository = productRepository;
        this.productBidRepository = productBidRepository;
    }

    public Product addProduct(Product product) throws ProductCreationException {

        if(product.getBidEndDate().isBefore(LocalDateTime.now()))
            throw new ProductCreationException("Bid End Date must be future date!");

        return this.productRepository.save(product);
    }

    public Optional<Product> getProduct(long productId) {
        return this.productRepository.findById(productId);
    }

    public Page<Product> getProducts(Integer pageNumber, Integer pageSize, String sortColumn) {

        Pageable pagable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortColumn);

        return this.productRepository.findAll(pagable);
    }

    public void deleteProduct(long productId) throws ProductDeletionException {

        Optional<Product> product = this.getProduct(productId);

        if(!product.isPresent()) throw new ProductDeletionException("Product does not exist!");

        if(product.get().getBidEndDate().isBefore(LocalDateTime.now()))
            throw new ProductDeletionException("Bid ended for this product. Deletion not allowed anymore!");

        boolean bidExisted = this.productBidRepository.existsProductBidByProductId(productId);
        if(bidExisted)
            throw new ProductDeletionException("Deletion not allowed. There are already existing bids placed!");

        this.productRepository.deleteById(productId);
    }

    public ProductViewDTO getProductBiddings(Long productId){

        Optional<Product> product = this.productRepository.findById(productId);
        if(!product.isPresent()) return null;

        ProductDTO productDTO = ProductMapper.INSTANCE.productToProductDTO(product.get());

        List<ProductBid> bidList = this.productBidRepository.findByProductIdOrderByBidAmountDesc(productId);

        ProductViewDTO productViewDTO = ProductViewDTO.builder()
                        .product(productDTO)
                        .bidList(bidList)
                        .build();

        return productViewDTO;
    }
}
