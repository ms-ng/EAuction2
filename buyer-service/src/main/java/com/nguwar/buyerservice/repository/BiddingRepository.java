package com.nguwar.buyerservice.repository;

import com.nguwar.buyerservice.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BiddingRepository extends JpaRepository<Bid, Long> {

    @Query("SELECT p FROM Bid p WHERE p.productId = :productId AND p.email = :email")
    Optional<Bid> findByProductIdAndEmail(@Param("productId") Long productId, @Param("email") String email);

}
