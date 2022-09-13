package com.nguwar.sellerservice.repository;

import com.nguwar.sellerservice.model.ProductBid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductBidRepositoryTest {

    @Autowired
    private ProductBidRepository productBidRepository;

    @BeforeEach
    void setUp() {
        ProductBid productBid1 = ProductBid.builder()
                .id(1L)
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();
        ProductBid productBid2 = ProductBid.builder()
                .id(2L)
                .productId(1L)
                .firstName("Mary")
                .lastName("Jane")
                .phone("1234567890")
                .email("mary@g.com")
                .bidAmount(BigDecimal.valueOf(200.00))
                .build();

        productBidRepository.save(productBid1);
        productBidRepository.save(productBid2);
    }

    @AfterEach
    void tearDown() {
        productBidRepository.deleteAll();
    }

    @Test
    void canFindByProductIdOrderByBidAmountDesc(){

        //when
        List<ProductBid> expected = productBidRepository.findByProductIdOrderByBidAmountDesc(1L);

        //then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getProductId()).isEqualTo(1L);
        assertTrue(expected.size() == 2);
        assertThat(expected.get(0).getFirstName()).isEqualTo("Mary");
    }

    @Test
    void cannotFindIfProductIdDoesNotExist(){
        //when
        List<ProductBid> expected = productBidRepository.findByProductIdOrderByBidAmountDesc(2L);

        //then
        assertTrue(expected.size() == 0);
    }

    @Test
    void productBidExistedIfProductIdFound(){

        //when
        boolean expected = productBidRepository.existsProductBidByProductId(1L);

        //then
        assertThat(expected).isTrue();
    }

    @Test
    void productBidNotExistedIfProductIdNotFound(){
        //when
        boolean expected = productBidRepository.existsProductBidByProductId(2L);

        //then
        assertThat(expected).isFalse();
    }

}