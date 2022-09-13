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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiddingCommandServiceTest {

    @InjectMocks
    private BiddingCommandService underTest;
    @Mock
    private BiddingRepository biddingRepository;
    @Mock
    private EventEmitter eventEmitter;
    @Mock
    private SellerServiceProxy sellerServiceProxy;

    @BeforeEach
    void setUp() {
        underTest = new BiddingCommandService(biddingRepository, eventEmitter, sellerServiceProxy);
    }

    @AfterEach
    void tearDown(){
    }

    @Test
    void canAddBid() {
        //Given

        NewBidDTO newBid = NewBidDTO.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        Bid bid = NewBidMapper.INSTANCE.newBidDTOToBid(newBid);

        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory("PAINTING");
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(1));

        given(sellerServiceProxy.retrieveProduct(newBid.getProductId())).willReturn(product);

        //When
        underTest.addBid(newBid);

        //Then
        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        verify(biddingRepository).save(captor.capture());

        Bid savedProduct = captor.getValue();
        assertThat(savedProduct).isEqualTo(bid);

        verify(eventEmitter).sendMessage(any(), any());
    }

    @Test
    void willThrowExceptionWhenAddingBid() {
        NewBidDTO newBid = NewBidDTO.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        Bid bid = NewBidMapper.INSTANCE.newBidDTOToBid(newBid);

        ProductDTO product = new ProductDTO();
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory("PAINTING");
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(1));

        given(sellerServiceProxy.retrieveProduct(newBid.getProductId())).willReturn(product);

        //When
        //Then
        assertThatThrownBy(() -> underTest.addBid(newBid))
                .isInstanceOf(BidCreationException.class);

        verify(biddingRepository, never()).save(any());
        verify(eventEmitter, never()).sendMessage(any(), any());
    }

    @Test
    void canUpdateBid() {

        Long productId = 1L;
        String buyerEmailId = "mary@g.com";
        BigDecimal newBidAmount = new BigDecimal(300);

        Bid bid = Bid.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("mary@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        ProductDTO product = new ProductDTO();
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory("PAINTING");
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(10));

        given(biddingRepository.findByProductIdAndEmail(productId,buyerEmailId)).willReturn(Optional.of(bid));
        given(sellerServiceProxy.retrieveProduct(productId)).willReturn(product);

        //When
        underTest.updateBid(productId, buyerEmailId, newBidAmount);

        //Then
        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        verify(biddingRepository).save(captor.capture());

        Bid savedProduct = captor.getValue();
        bid.setBidAmount(newBidAmount);
        assertThat(savedProduct).isEqualTo(bid);

        verify(eventEmitter).sendMessage(any(), any());
    }

    @Test
    void willThrowExceptionWhenUpdatingBid() {

        Long productId = 1L;
        String buyerEmailId = "mary@g.com";
        BigDecimal newBidAmount = new BigDecimal(300);

        Bid bid = Bid.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("mary@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        ProductDTO product = new ProductDTO();
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory("PAINTING");
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().minusDays(10));

        given(biddingRepository.findByProductIdAndEmail(productId,buyerEmailId)).willReturn(Optional.of(bid));
        given(sellerServiceProxy.retrieveProduct(productId)).willReturn(product);

        //When
        //Then
        assertThatThrownBy(() -> underTest.updateBid(productId,buyerEmailId,newBidAmount))
                .isInstanceOf(BidUpdateException.class);

        verify(biddingRepository, never()).save(any());
        verify(eventEmitter, never()).sendMessage(any(), any());
    }

}