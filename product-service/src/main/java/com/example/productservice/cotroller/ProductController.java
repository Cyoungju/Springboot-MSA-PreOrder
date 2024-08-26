package com.example.productservice.cotroller;

import com.example.productservice.core.utils.ApiUtils;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.entity.ProductStatus;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Page<ProductDto> productDtos = productService.findAll(pageable);
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
