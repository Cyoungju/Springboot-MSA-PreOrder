package com.example.orderservice.controller;


import com.example.orderservice.dto.WishListItemDto;
import com.example.orderservice.dto.WishListResponseDto;
import com.example.orderservice.service.WishListService;
import com.example.orderservice.core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RequiredArgsConstructor
@RequestMapping("/api/wishList")
@RestController
public class WishListController {

    private final WishListService wishListService;

    @GetMapping
    public ResponseEntity<?> getWishList(@RequestHeader("X-Authenticated-User") String email){
        WishListResponseDto wishList = wishListService.wishList(email);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping
    public ResponseEntity<?> addWish(@RequestBody WishListItemDto wishListItemListDto, @RequestHeader("X-Authenticated-User") String email){
        WishListResponseDto wishList = wishListService.addWishList(wishListItemListDto, email);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWish(@PathVariable Long id, @RequestHeader("X-Authenticated-User") String email){
        WishListResponseDto wishList = wishListService.deleteWishListItem(id, email);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }
}
