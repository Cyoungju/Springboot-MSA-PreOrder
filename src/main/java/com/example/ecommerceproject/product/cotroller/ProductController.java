package com.example.ecommerceproject.product.cotroller;

import com.example.ecommerceproject.core.utils.ApiUtils;
import com.example.ecommerceproject.product.dto.ProductDto;
import com.example.ecommerceproject.product.entity.ProductStatus;
import com.example.ecommerceproject.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // post /api/product
    // delete /api/product/{id}
    // patch /api/products/{id}


    // 추후 paging 처리
    // 전체 상품 조회
    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(page = 1, size = 10) Pageable pageable) {
        Page<ProductDto> productDtos = productService.findAll(ProductStatus.AVAILABLE, pageable);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(productDtos);
        return ResponseEntity.ok(apiResult);
    }

    // 상세페이지 조회 - 개별상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        ProductDto productDto = productService.findById(id);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(productDto);
        return ResponseEntity.ok(apiResult);
    }
}
