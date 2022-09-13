package com.nguwar.buyerservice.dto.mapper;

import com.nguwar.buyerservice.dto.NewBidDTO;
import com.nguwar.buyerservice.model.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewBidMapper {

    NewBidMapper INSTANCE = Mappers.getMapper(NewBidMapper.class);
    Bid newBidDTOToBid(NewBidDTO newBidDTO);
}
