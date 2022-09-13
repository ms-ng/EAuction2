package com.nguwar.buyerservice.repository;

import com.nguwar.buyerservice.BuyerServiceApplication;
import com.nguwar.buyerservice.model.Bid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.main.lazy-initialization=true",
        classes = {BuyerServiceApplication.class})
class BiddingRepositoryTest {

    @Autowired
    private BiddingRepository biddingRepository;

    @BeforeEach
    void setUp() {
        Bid bid1 = Bid.builder()
                .id(1L)
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();
        Bid bid2 = Bid.builder()
                .id(2L)
                .productId(1L)
                .firstName("Mary")
                .lastName("Jane")
                .phone("1234567890")
                .email("mary@g.com")
                .bidAmount(BigDecimal.valueOf(200.00))
                .build();

        biddingRepository.save(bid1);
        biddingRepository.save(bid2);
    }

    @AfterEach
    void tearDown() {
        biddingRepository.deleteAll();
    }

    @Test
    void canFindByProductIdAndEmail() {

        //when
        Optional<Bid> expected = biddingRepository.findByProductIdAndEmail(1L, "mary@g.com");

        //then
        assertThat(expected.get()).isNotNull();
        assertThat(expected.get().getProductId()).isEqualTo(1L);
        assertThat(expected.get().getFirstName()).isEqualTo("Mary");
    }

    @Test
    void cannotFindByProductIdAndEmailIfProductNotExisted() {
        //when
        Optional<Bid> expected = biddingRepository.findByProductIdAndEmail(2L, "mary@g.com");

        //then
        assertTrue(expected.isPresent() == false);
    }
}