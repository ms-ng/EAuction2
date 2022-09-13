package com.nguwar.sellerservice.service;

import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import com.nguwar.sellerservice.dto.mapper.ProductBidMapper;
import com.nguwar.sellerservice.exception.ProductCreationException;
import com.nguwar.sellerservice.exception.ProductDeletionException;
import com.nguwar.sellerservice.model.Product;
import com.nguwar.sellerservice.model.ProductBid;
import com.nguwar.sellerservice.model.ProductCategory;
import com.nguwar.sellerservice.repository.ProductBidRepository;
import com.nguwar.sellerservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductBidServiceTest {

    @InjectMocks
    private ProductBidService underTest;

    @Mock
    private ProductBidRepository productBidRepository;

    @BeforeEach
    void setUp() {
        underTest = new ProductBidService(productBidRepository);
    }

    @AfterEach
    void tearDown(){
    }

    @Test
    void canSaveProductBid() {
        //Given
        CreatedProductBidEvent createdProductBidEvent = CreatedProductBidEvent.builder()
                .id(1L)
                .productId(1L)
                .bidAmount(new BigDecimal(100.00))
                .firstName("Ellen")
                .lastName("Smith")
                .email("ellen@g.com")
                .phone("1234567890")
                .build();

        //When
        underTest.saveProductBid(createdProductBidEvent);

        ProductBid productBid = ProductBidMapper.INSTANCE.createdProductBidToProductBid(createdProductBidEvent);

        //Then
        ArgumentCaptor<ProductBid> captor = ArgumentCaptor.forClass(ProductBid.class);
        verify(productBidRepository).save(captor.capture());

        ProductBid savedProduct = captor.getValue();
        assertThat(savedProduct).isEqualTo(productBid);
    }

}