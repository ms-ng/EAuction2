package com.nguwar.sellerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    //Product Name is not null, min 5 and max 30 characters
    @NotBlank(message = "Product name is mandatory")
    @Size(min = 5, max = 30, message = "Product name must be between 5 and 30 characters")
    private String shortName;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @PositiveOrZero
    private BigDecimal startingPrice;

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

    @CreationTimestamp
    private Date createdDate;
}
