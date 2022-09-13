package com.nguwar.buyerservice.service;

import com.nguwar.buyerservice.emitter.EventEmitter;
import com.nguwar.buyerservice.dto.NewBidDTO;
import com.nguwar.buyerservice.dto.ProductDTO;
import com.nguwar.buyerservice.dto.mapper.NewBidMapper;
import com.nguwar.buyerservice.exception.BidCreationException;
import com.nguwar.buyerservice.exception.BidUpdateException;
import com.nguwar.buyerservice.model.Bid;
import com.nguwar.buyerservice.proxy.SellerServiceProxy;
import com.nguwar.buyerservice.repository.BiddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class BiddingCommandService {

    @Value("${kafka-topic-bid-created}")
    private String kafkaTopic_BidCreated;

    @Value("${kafka-topic-bid-updated}")
    private String kafkaTopic_BidUpdated;

    private EventEmitter eventEmitter;

    private SellerServiceProxy sellerServiceProxy;

    private BiddingRepository biddingRepository;

    @Autowired
    public BiddingCommandService(BiddingRepository biddingRepository,
                                 EventEmitter eventEmitter,
                                 SellerServiceProxy sellerServiceProxy) {
        this.biddingRepository = biddingRepository;
        this.eventEmitter = eventEmitter;
        this.sellerServiceProxy = sellerServiceProxy;
    }

    public Bid addBid(NewBidDTO newBid) throws BidCreationException{
        Optional<Bid> optionalBid = Optional.empty();

        ProductDTO product = this.sellerServiceProxy.retrieveProduct(newBid.getProductId());

        if(product.getId() == null)
            throw new BidCreationException("Product Id does not exists!");

        if(product.getBidEndDate().isBefore(LocalDateTime.now()))
            throw new BidCreationException("Bid has been already ended!");

        Optional<Bid> duplicateBid = biddingRepository.findByProductIdAndEmail(newBid.getProductId(), newBid.getEmail());
        if(duplicateBid.isPresent())
            throw new BidCreationException("Duplicate Bid placed by same buyer!");

        Bid bid = NewBidMapper.INSTANCE.newBidDTOToBid(newBid);
        Bid createdBid = this.biddingRepository.save(bid);

        this.eventEmitter.sendMessage(kafkaTopic_BidCreated, createdBid);

        return createdBid;
    }

    public void updateBid(Long productId, String buyerEmailId, BigDecimal newBidAmount)
                    throws BidUpdateException {

        Optional<Bid> optionalBid = this.biddingRepository.findByProductIdAndEmail(productId,buyerEmailId);

        if(optionalBid.isPresent() == false)
            throw new BidUpdateException("Bid Not Found to update!");

        ProductDTO product = this.sellerServiceProxy.retrieveProduct(productId);
        if(product.getBidEndDate().isBefore(LocalDateTime.now()))
            throw new BidUpdateException("Bid has been already ended!");

        Bid bid = optionalBid.get();
        bid.setBidAmount(newBidAmount);
        this.biddingRepository.save(bid);

        this.eventEmitter.sendMessage(kafkaTopic_BidUpdated, bid);
    }

}
