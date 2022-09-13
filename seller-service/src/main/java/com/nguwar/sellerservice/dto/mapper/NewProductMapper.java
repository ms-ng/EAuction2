package com.nguwar.sellerservice.dto.mapper;

import com.nguwar.sellerservice.dto.NewProductDTO;
import com.nguwar.sellerservice.model.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewProductMapper {

    NewProductMapper INSTANCE = Mappers.getMapper(NewProductMapper.class);

    @Mappings({
            @Mapping(source = "newProductDTO.productCategory", target = "productCategory"),
    })
    Product newProductDTOToProduct(NewProductDTO newProductDTO);
}
