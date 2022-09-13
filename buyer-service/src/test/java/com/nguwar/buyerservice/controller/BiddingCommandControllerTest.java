package com.nguwar.buyerservice.controller;

import com.nguwar.buyerservice.dto.NewBidDTO;
import com.nguwar.buyerservice.dto.mapper.NewBidMapper;
import com.nguwar.buyerservice.exception.BidCreationException;
import com.nguwar.buyerservice.exception.BidUpdateException;
import com.nguwar.buyerservice.model.Bid;
import com.nguwar.buyerservice.service.BiddingCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BiddingCommandController.class)
class BiddingCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BiddingCommandService service;

    @Test
    void canPlaceBid() throws Exception {

        NewBidDTO newBid = NewBidDTO.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        Bid bid = NewBidMapper.INSTANCE.newBidDTOToBid(newBid);

        given(service.addBid(newBid)).willReturn(bid);

        mockMvc.perform(
                        post("/buyer-service/v1/place-bid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(newBid)))
                .andExpect(status().isOk());
    }

    @Test
    public void willGetBadRequestWhenPlacingBid() throws Exception {
        NewBidDTO newBid = NewBidDTO.builder()
                .productId(1L)
                .firstName("Alex")
                .lastName("John")
                .phone("1234567890")
                .email("alex@g.com")
                .bidAmount(BigDecimal.valueOf(100.00))
                .build();

        Bid bid = NewBidMapper.INSTANCE.newBidDTOToBid(newBid);

        Mockito.doThrow(
                        new BidCreationException("Bid has been already ended!"))
                .when(service).addBid(newBid);

        mockMvc.perform(
                        post("/buyer-service/v1/place-bid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(newBid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canUpdateBid() throws Exception {

        Long productId = 1L;
        String buyerEmailId = "mary@g.com";
        BigDecimal newBidAmount = new BigDecimal(300);

        doNothing().when(service).updateBid(productId,buyerEmailId, newBidAmount);

        mockMvc.perform(
                        put("/buyer-service/v1/update-bid/" + productId + "/" + buyerEmailId + "/" + newBidAmount)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void willGetBadRequestWhenUpdatingBid() throws Exception {

        Long productId = 1L;
        String buyerEmailId = "mary@g.com";
        BigDecimal newBidAmount = new BigDecimal(300);

        Mockito.doThrow(
                        new BidUpdateException("Bid End Date must be future date!"))
                .when(service).updateBid(productId,buyerEmailId, newBidAmount);

        mockMvc.perform(
                        put("/buyer-service/v1/update-bid/" + productId + "/" + buyerEmailId + "/" + newBidAmount)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}