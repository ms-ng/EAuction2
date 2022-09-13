package com.nguwar.buyerservice.controller;

import com.nguwar.buyerservice.dto.NewBidDTO;
import com.nguwar.buyerservice.exception.BidCreationException;
import com.nguwar.buyerservice.exception.BidUpdateException;
import com.nguwar.buyerservice.model.Bid;
import com.nguwar.buyerservice.service.BiddingCommandService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
public class BiddingCommandController {

    private final Logger logger = LoggerFactory.getLogger(BiddingCommandController.class);

    private BiddingCommandService biddingCommandService;

    @Autowired
    public BiddingCommandController(BiddingCommandService biddingCommandService) {
        this.biddingCommandService = biddingCommandService;
    }

    @PostMapping("/e-auction/api/v1/buyer/place-bid")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallbackResponse")
    public ResponseEntity<String> placeBid(@Valid @RequestBody NewBidDTO newBid){

        try {
            Bid createdBid = biddingCommandService.addBid(newBid);
            return ResponseEntity.ok("New Bid Placed successfully");

        } catch(BidCreationException ex) {
            logger.error("result --> {} " , ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/e-auction/api/v1/buyer/update-bid/{productId}/{buyerEmailId}/{newBidAmount}")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallbackResponse")
    public ResponseEntity<String> updateBid(@PathVariable Long productId,
                                                @PathVariable String buyerEmailId,
                                                @PathVariable BigDecimal newBidAmount){

        try {
            this.biddingCommandService.updateBid(productId, buyerEmailId, newBidAmount);
            return ResponseEntity.ok("bid price updated!");

        } catch(BidUpdateException ex) {
            logger.error("result --> {} " , ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    public ResponseEntity<String> fallbackResponse(Exception ex){ return ResponseEntity.internalServerError().body(ex.getMessage());}


}
