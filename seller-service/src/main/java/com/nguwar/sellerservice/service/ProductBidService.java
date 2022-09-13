package com.nguwar.sellerservice.service;

import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import com.nguwar.sellerservice.dto.mapper.ProductBidMapper;
import com.nguwar.sellerservice.model.ProductBid;
import com.nguwar.sellerservice.repository.ProductBidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductBidService {

    private ProductBidRepository productBidRepository;

    @Autowired
    public ProductBidService(ProductBidRepository productBidRepository) {
        this.productBidRepository = productBidRepository;
    }

    public void saveProductBid(CreatedProductBidEvent createdProductBidEvent){
        ProductBid productBid = ProductBidMapper.INSTANCE.createdProductBidToProductBid(createdProductBidEvent);
        this.productBidRepository.save(productBid);
    }
}
