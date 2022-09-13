package com.nguwar.sellerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductDTO {

    //Product Name is not null, min 5 and max 30 characters
    @NotBlank(message = "Product name is mandatory")
    @Size(min = 5, max = 30, message = "Product name must be between 5 and 30 characters")
    private String shortName;

    private String description;

    @NotBlank(message = "Product Category is mandatory")
    @Pattern(regexp = "Painting|Sculptor|Ornament", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String productCategory;

    @PositiveOrZero
    private BigDecimal startingPrice;

    @Future(message = "Bind End date must be future date")
    private LocalDateTime bidEndDate;

    //firstName is not null, min 5 and max 30 characters
    @NotBlank(message = "First name is mandatory")
    @Size(min = 3, max = 25, message = "First name must be between 3 and 25 characters")
    private String firstName;

    //lastName is not null, min 3 and max 25 characters.
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 3, max = 25, message = "Last name must be between 3 and 25 characters")
    private String lastName;

    private String address;
    private String city;
    private String state;
    private String postalCode;

    //mobile is not null, min 10 and max 10 character and all must be numeric.
    @NotBlank(message = "Phone number is mandatory")
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phone;

    //email is not null, and it should be valid email pattern, containing a single @.
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;
}
