package com.nguwar.sellerservice.service;

import com.nguwar.sellerservice.SellerServiceApplication;
import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(classes = SellerServiceApplication.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaConsumerTest {

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
    public void it_should_receive_bid_event() throws InterruptedException, IOException {
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