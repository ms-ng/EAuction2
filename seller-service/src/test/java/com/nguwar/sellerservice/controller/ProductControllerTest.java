package com.nguwar.sellerservice.controller;

import com.nguwar.sellerservice.dto.CreatedProductBidEvent;
import com.nguwar.sellerservice.dto.NewProductDTO;
import com.nguwar.sellerservice.dto.ProductDTO;
import com.nguwar.sellerservice.dto.ProductViewDTO;
import com.nguwar.sellerservice.dto.mapper.NewProductMapper;
import com.nguwar.sellerservice.dto.mapper.ProductMapper;
import com.nguwar.sellerservice.exception.ProductCreationException;
import com.nguwar.sellerservice.exception.ProductDeletionException;
import com.nguwar.sellerservice.model.Product;
import com.nguwar.sellerservice.model.ProductBid;
import com.nguwar.sellerservice.model.ProductCategory;
import com.nguwar.sellerservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @MockBean
    private ProductMapper productMapper;

    @Test
    public void canCreateProduct() throws Exception {
        NewProductDTO newProductDTO = new NewProductDTO();
        newProductDTO.setShortName("P0001");
        newProductDTO.setDescription("Product 1");
        newProductDTO.setProductCategory("PAINTING");
        newProductDTO.setStartingPrice(new BigDecimal(10));
        newProductDTO.setBidEndDate(LocalDateTime.now().plusDays(10));
        newProductDTO.setFirstName("Jennie");
        newProductDTO.setLastName("Cho");
        newProductDTO.setAddress("Waston Street");
        newProductDTO.setCity("Toronto");
        newProductDTO.setState("Ontario");
        newProductDTO.setPostalCode("123456");
        newProductDTO.setPhone("1234567890");
        newProductDTO.setEmail("jennie@g.com");

        Product product = NewProductMapper.INSTANCE.newProductDTOToProduct(newProductDTO);
        product.setId(1L);
        given(service.addProduct(product)).willReturn(product);

        mockMvc.perform(
                post("/seller-service/v1/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(newProductDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void willGetBadRequestWhenCreatingProduct() throws Exception {
        NewProductDTO newProductDTO = new NewProductDTO();
        newProductDTO.setShortName("P0001");
        newProductDTO.setDescription("Product 1");
        newProductDTO.setProductCategory("PAINTING");
        newProductDTO.setStartingPrice(new BigDecimal(10));
        newProductDTO.setBidEndDate(LocalDateTime.now().minusDays(10));
        newProductDTO.setFirstName("Jennie");
        newProductDTO.setLastName("Cho");
        newProductDTO.setAddress("Waston Street");
        newProductDTO.setCity("Toronto");
        newProductDTO.setState("Ontario");
        newProductDTO.setPostalCode("123456");
        newProductDTO.setPhone("1234567890");
        newProductDTO.setEmail("jennie@g.com");

        Product product = NewProductMapper.INSTANCE.newProductDTOToProduct(newProductDTO);

        Mockito.doThrow(
                        new ProductCreationException("Bid End Date must be future date!"))
                .when(service).addProduct(product);

        mockMvc.perform(
                        post("/seller-service/v1/add-product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(newProductDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void canGetProduct() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory(ProductCategory.PAINTING);
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(1));
        product.setFirstName("Jennie");
        product.setLastName("Cho");
        product.setAddress("Waston Street");
        product.setCity("Toronto");
        product.setState("Ontario");
        product.setPostalCode("123456");
        product.setPhone("1234567890");
        product.setEmail("jennie@g.com");

        given(service.getProduct(product.getId())).willReturn(Optional.of(product));

        mockMvc.perform(get("/seller-service/v1/product/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("shortName", is(product.getShortName())));
    }

    @Test
    public void willGetNotFoundWhenGettingProduct() throws Exception {

        Product product = new Product();
        product.setId(1L);

        given(service.getProduct(anyLong())).willReturn(Optional.ofNullable(null));

        mockMvc.perform(get("/seller-service/v1/product/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void canDeleteProduct() throws Exception {

        Product product = new Product();
        product.setId(1L);

        doNothing().when(service).deleteProduct(product.getId());

        mockMvc.perform(delete("/seller-service/v1/delete/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("product deleted successfully!"));
    }

    @Test
    public void willGetBadRequestWhenDeletingProduct() throws Exception {

        Product product = new Product();
        product.setId(1L);

        Mockito.doThrow(
                new ProductDeletionException("Product does not exist!"))
                .when(service).deleteProduct(product.getId());

        mockMvc.perform(delete("/seller-service/v1/delete/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void canGetProductList() throws Exception {

        Pageable pagable = PageRequest.of(0, 10, Sort.Direction.ASC, "shortName");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setShortName("P0001");
        productDTO.setDescription("Product 1");
        productDTO.setProductCategory("PAINTING");
        productDTO.setStartingPrice(new BigDecimal(10));
        productDTO.setBidEndDate(LocalDateTime.now().plusDays(1));

        Product product = new Product();
        product.setId(1L);
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory(ProductCategory.PAINTING);
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(1));
        product.setFirstName("Jennie");
        product.setLastName("Cho");
        product.setAddress("Waston Street");
        product.setCity("Toronto");
        product.setState("Ontario");
        product.setPostalCode("123456");
        product.setPhone("1234567890");
        product.setEmail("jennie@g.com");

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Page<Product> products = new PageImpl<>(productList,pagable,1);

        given(service.getProducts(0,10, "shortName")).willReturn(products);
        given(productMapper.productToProductDTO(product)).willReturn(productDTO);

        mockMvc.perform(get("/seller-service/v1/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void canGetProductBiddings() throws Exception {

        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory("PAINTING");
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().plusDays(1));

        ProductBid bid = ProductBid.builder()
                .id(1L)
                .productId(1L)
                .bidAmount(new BigDecimal(100.00))
                .firstName("Ellen")
                .lastName("Smith")
                .email("ellen@g.com")
                .phone("1234567890")
                .build();

        List<ProductBid> bidlist = new ArrayList<>();
        bidlist.add(bid);

        ProductViewDTO productViewDTO = ProductViewDTO.builder()
                .product(product)
                .bidList(bidlist)
                .build();

        given(service.getProductBiddings(product.getId())).willReturn(productViewDTO);

        mockMvc.perform(get("/seller-service/v1/show-bids/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}