package com.nguwar.sellerservice.listener;

import com.nguwar.sellerservice.controller.ProductController;
import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import com.nguwar.sellerservice.service.ProductBidService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductBidService productBidService;

    @KafkaListener(topics = "bid-created-ee")
    public void listenBidCreated(ConsumerRecord<Long, CreatedProductBidEvent> payload) {
        logger.info("Received Kafka Message : {}", payload.value());
        productBidService.saveProductBid(payload.value());
    }

    @KafkaListener(topics = "bid-updated-ee")
    public void listenBidUpdated(ConsumerRecord<Long, CreatedProductBidEvent> payload) {
        logger.info("Received Kafka Message : {}", payload.value());
        productBidService.saveProductBid(payload.value());
    }
}
