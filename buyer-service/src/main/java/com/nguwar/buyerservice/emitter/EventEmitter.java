package com.nguwar.buyerservice.emitter;

import com.nguwar.buyerservice.controller.BiddingCommandController;
import com.nguwar.buyerservice.dto.CreatedProductBidEvent;
import com.nguwar.buyerservice.model.Bid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class EventEmitter {

    private final Logger logger = LoggerFactory.getLogger(EventEmitter.class);

    @Autowired
    private KafkaTemplate<Long, CreatedProductBidEvent> kafkaTemplate;

    public void sendMessage(String topicName, Bid bid) {

        CreatedProductBidEvent createdProductBid = CreatedProductBidEvent.builder()
                .productId(bid.getProductId())
                .id(bid.getId())
                .firstName(bid.getFirstName())
                .lastName(bid.getLastName())
                .address(bid.getAddress())
                .city(bid.getCity())
                .state(bid.getState())
                .postalCode(bid.getPostalCode())
                .phone(bid.getPhone())
                .email(bid.getEmail())
                .bidAmount(bid.getBidAmount())
                .build();

        ListenableFuture<SendResult<Long, CreatedProductBidEvent>> future =
                kafkaTemplate.send(topicName, createdProductBid.getProductId(), createdProductBid);

        future.addCallback(new ListenableFutureCallback<SendResult<Long, CreatedProductBidEvent>>() {

            @Override
            public void onSuccess(SendResult<Long, CreatedProductBidEvent> result) {
                logger.info("Sent message=[{}] with offset=[{}]",
                        createdProductBid.getProductId(),
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Unable to send message=[{}] due to : {}",
                        createdProductBid.getProductId(),
                        ex.getMessage());
            }
        });
    }
}
