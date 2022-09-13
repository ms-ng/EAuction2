package com.nguwar.buyerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguwar.buyerservice.BuyerServiceApplication;
import com.nguwar.buyerservice.dto.CreatedProductBidEvent;
import com.nguwar.buyerservice.model.Bid;
import com.nguwar.buyerservice.repository.BiddingRepository;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;



@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" })
public class  KafkaProducerTest {


    private static final String TOPIC = "bid-created-test";
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    BlockingQueue<ConsumerRecord<Long, CreatedProductBidEvent>> records;
    KafkaMessageListenerContainer<Long, CreatedProductBidEvent> container;

    @BeforeEach
    public void setUp() throws Exception{

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        JsonDeserializer<CreatedProductBidEvent> deserializer = new JsonDeserializer<>(CreatedProductBidEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        DefaultKafkaConsumerFactory<Long, CreatedProductBidEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), deserializer);

        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<Long, CreatedProductBidEvent>) records::add);
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    public void tearDown() throws Exception {
        container.stop();
    }

    @Test
    public void it_should_send_bid_event() throws InterruptedException, IOException {
        CreatedProductBidEvent bidEvent = CreatedProductBidEvent.builder()
                .productId(1)
                .build();

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        JsonSerializer<CreatedProductBidEvent> serializer = new JsonSerializer<>();
        Producer<Long, CreatedProductBidEvent> producer = new DefaultKafkaProducerFactory<>(configs, new LongSerializer(), serializer).createProducer();
        producer.send(new ProducerRecord<>(TOPIC, bidEvent.getProductId(), CreatedProductBidEvent.builder().productId(bidEvent.getProductId()).build()));
        producer.flush();

        ConsumerRecord<Long, CreatedProductBidEvent> singleRecord = records.poll(100, TimeUnit.MILLISECONDS);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value().getProductId()).isEqualTo(bidEvent.getProductId());
    }

}