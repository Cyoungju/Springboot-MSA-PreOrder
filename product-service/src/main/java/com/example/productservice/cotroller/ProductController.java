package com.example.productservice.cotroller;

import com.example.productservice.core.utils.ApiUtils;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.dto.SaleTimeDto;
import com.example.productservice.entity.ProductStatus;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Slf4j
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


    // 수량 검증
    @GetMapping("/stock/{productId}")
    public ResponseEntity<?> getStock (@PathVariable Long productId){
        int productStock = productService.getStock(productId);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(productStock);
        return ResponseEntity.ok(apiResult);
    }

    //todo : 권한 설정 - admin 권한만 접근 가능하게 (@RequestHeader("X-Authenticated-User") String email)
    //세일 시간 변경
    @PostMapping("/sale-time/{id}")
    public ResponseEntity<?> saleTime(@PathVariable Long id, @RequestBody SaleTimeDto saleTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime saleStartTime = LocalTime.parse(saleTime.getSaleTime(), formatter);

        productService.changeSaleTime(id,saleStartTime);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success("상품 "+id+ " 타임 세일 시간 (" + saleStartTime + ") 설정되었습니다");
        return ResponseEntity.ok(apiResult);
    }

}
