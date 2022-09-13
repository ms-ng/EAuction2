package com.nguwar.sellerservice.service;

import com.nguwar.sellerservice.exception.ProductCreationException;
import com.nguwar.sellerservice.exception.ProductDeletionException;
import com.nguwar.sellerservice.model.Product;
import com.nguwar.sellerservice.model.ProductBid;
import com.nguwar.sellerservice.model.ProductCategory;
import com.nguwar.sellerservice.repository.ProductBidRepository;
import com.nguwar.sellerservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService underTest;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductBidRepository productBidRepository;

    @BeforeEach
    void setUp() {
        underTest = new ProductService(productRepository, productBidRepository);
    }

    @AfterEach
    void tearDown(){
    }

    @Test
    void canAddProduct() {
        //Given
        Product product = new Product();
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

        //When
        underTest.addProduct(product);

        //Then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product savedProduct = captor.getValue();
        assertThat(savedProduct).isEqualTo(product);
    }

    @Test
    void willThrowErrorWhenAddingProduct() {
        //Given
        Product product = new Product();
        product.setShortName("P0001");
        product.setDescription("Product 1");
        product.setProductCategory(ProductCategory.PAINTING);
        product.setStartingPrice(new BigDecimal(10));
        product.setBidEndDate(LocalDateTime.now().minusDays(1));
        product.setFirstName("Jennie");
        product.setLastName("Cho");
        product.setAddress("Waston Street");
        product.setCity("Toronto");
        product.setState("Ontario");
        product.setPostalCode("123456");
        product.setPhone("1234567890");
        product.setEmail("jennie@g.com");

        //When
        //Then
        assertThatThrownBy(() -> underTest.addProduct(product))
                .isInstanceOf(ProductCreationException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void canGetProduct() {
        //Given
        //when
        underTest.getProduct(1);
        //then
        verify(productRepository).findById(1L);

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

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        // When looking for all items
        assertThat(underTest.getProduct(1)).isEqualTo(Optional.of(product));
    }

    @Test
    void canGetAllProducts() {
        //Given
        Pageable pagable = PageRequest.of(0, 10, Sort.Direction.ASC, "shortName");

        //when
        underTest.getProducts(0,10,"shortName");
        //then
        verify(productRepository).findAll(pagable);

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

        given(productRepository.findAll(pagable)).willReturn(products);
        // When looking for all items
        assertTrue(underTest.getProducts(0, 10, "shortName").getContent().size() == 1);
    }

    @Test
    void canGetProductBiddings() {

        //when
        underTest.getProductBiddings(1L);
        verify(productRepository).findById(1L);
        //then

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

        ProductBid bid = ProductBid.builder()
                .id(1L)
                .productId(1L)
                .bidAmount(new BigDecimal(100.00))
                .firstName("Ellen")
                .lastName("Smith")
                .email("ellen@g.com")
                .phone("1234567890")
                .build();

        List<ProductBid> bidList = new ArrayList<>();
        bidList.add(bid);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        //verify(productBidRepository).findByProductIdOrderByBidAmountDesc(1L);
        given(productBidRepository.findByProductIdOrderByBidAmountDesc(1L)).willReturn(bidList);

        // When looking for all items
        assertThat(underTest.getProductBiddings(1L).getProduct().getShortName()).isEqualTo("P0001");
        assertTrue(underTest.getProductBiddings(1L).getBidList().size() == 1);

    }

    @Test
    void canDeleteProduct() {
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

        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        underTest.deleteProduct(product.getId());

        verify(productRepository).deleteById(product.getId());
    }

    @Test
    void willThrowErrorWhenDeletingProduct() {
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

        given(productRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> underTest.deleteProduct(product.getId()))
                .isInstanceOf(ProductDeletionException.class);

        verify(productRepository, never()).delete(any());
    }


}