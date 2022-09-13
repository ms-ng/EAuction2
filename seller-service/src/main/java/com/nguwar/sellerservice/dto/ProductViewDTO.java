package com.nguwar.sellerservice.dto;

import com.nguwar.sellerservice.model.ProductBid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDTO {
    private ProductDTO product;
    private List<ProductBid> bidList;
}
