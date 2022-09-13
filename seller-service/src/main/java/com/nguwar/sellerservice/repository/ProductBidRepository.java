package com.nguwar.sellerservice.repository;

import com.nguwar.sellerservice.model.ProductBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBidRepository extends JpaRepository<ProductBid, Long> {

    @Query("SELECT p FROM ProductBid p WHERE p.productId = :productId ORDER BY p.bidAmount DESC")
    List<ProductBid> findByProductIdOrderByBidAmountDesc(@Param("productId") Long productId);

    @Query("SELECT CASE WHEN count(p)> 0 THEN true ELSE false END FROM ProductBid p WHERE p.productId = :productId")
    boolean existsProductBidByProductId(@Param("productId") Long productId);

}
