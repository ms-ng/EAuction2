package com.nguwar.sellerservice.dto.mapper;

import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import com.nguwar.sellerservice.model.ProductBid;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductBidMapper {

    ProductBidMapper INSTANCE = Mappers.getMapper(ProductBidMapper.class);

    ProductBid createdProductBidToProductBid(CreatedProductBidEvent createdProductBidEvent);
}
