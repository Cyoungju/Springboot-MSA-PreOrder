package com.example.ecommerceproject.wishList.controller;


import com.example.ecommerceproject.core.utils.ApiUtils;
import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.wishList.dto.WishListItemDto;
import com.example.ecommerceproject.wishList.dto.WishListItemListDto;
import com.example.ecommerceproject.wishList.dto.WishListResponseDto;
import com.example.ecommerceproject.wishList.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


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
