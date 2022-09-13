package com.nguwar.buyerservice.proxy;

import com.nguwar.buyerservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name="seller-service")
public interface SellerServiceProxy {

    @GetMapping("/e-auction/api/v1/seller/product/{productId}")
    public ProductDTO retrieveProduct(
            @PathVariable long productId);
}
