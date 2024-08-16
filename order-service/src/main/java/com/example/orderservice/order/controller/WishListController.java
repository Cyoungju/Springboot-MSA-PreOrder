package com.example.orderservice.order.controller;


import com.example.orderservice.core.utils.ApiUtils;
import com.example.orderservice.order.dto.WishListItemDto;
import com.example.orderservice.order.dto.WishListResponseDto;
import com.example.orderservice.order.service.WishListService;
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
    public ResponseEntity<?> getWishList(Principal principal){
        String username = principal.getName();
        WishListResponseDto wishList = wishListService.wishList(username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping
    public ResponseEntity<?> addWish(@RequestBody WishListItemDto wishListItemListDto, Principal principal){
        String username = principal.getName();

        WishListResponseDto wishList = wishListService.addWishList(wishListItemListDto, username);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWish(@PathVariable Long id, Principal principal){
        String username = principal.getName();

        WishListResponseDto wishList = wishListService.deleteWishListItem(id, username);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(wishList);
        return ResponseEntity.ok(apiResult);
    }
}
