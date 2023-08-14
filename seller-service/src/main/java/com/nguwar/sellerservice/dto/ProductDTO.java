package com.nguwar.sellerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String shortName;
    private String description;
    private String productCategory;
    private BigDecimal startingPrice;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime bidEndDate;
}
