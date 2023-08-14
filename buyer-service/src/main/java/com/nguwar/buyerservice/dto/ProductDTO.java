package com.nguwar.buyerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private LocalDateTime bidEndDate;
}
