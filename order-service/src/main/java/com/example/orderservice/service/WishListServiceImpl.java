package com.example.orderservice.service;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.*;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.repository.WishListItemRepository;
import com.example.orderservice.entity.WishList;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.repository.WishListRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class WishListServiceImpl implements WishListService {
    private final WishListItemRepository wishListItemRepository;
    private final WishListRepository wishListRepository;
    private final ProductServiceClient productServiceClient;

    // 전체 상품 조회
    @Override
    public WishListResponseDto wishList(String email) {

        List<WishListItem> items = wishListItemRepository.findByWishListMemberEmail(email); // ID로 아이템 조회

        WishList wishList = wishListRepository.findByMemberEmail(email)
                .orElseGet(() -> createNewWishList(email));

        Long totalPrice = wishList.getTotalPrice();
        List<WishListItemListDto> itemDtos= items.stream()
                .map(item -> new WishListItemListDto(item.getId(), item.getCount(), item.getProductName(), item.getProductPrice()))
                .collect(Collectors.toList());

        return new WishListResponseDto(itemDtos, totalPrice);

    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    @Retry(name = "productService", fallbackMethod = "productServiceFallback")
    //@TimeLimiter(name = "productService")
    public WishListResponseDto addWishList(WishListItemDto wishListItemDto, String email) {

        // 상품 추가
        int wishListItemCount = wishListItemDto.getCount();

        Long productId = wishListItemDto.getProductId();

        // 이메일을 통해 WishList를 찾기
        WishList wishList = wishListRepository.findByMemberEmail(email)
                .orElseGet(() -> createNewWishList(email));

        // WishList에 해당하는 productID를 가진 wishListItem이 있는지 확인
        Optional<WishListItem> wishListItemOptional = wishListItemRepository.findByWishListAndProductId(wishList, productId);

        if(wishListItemOptional.isPresent()){
            // 상품이 있을 경우
            // 상품 확인
            WishListItem wishListItem = wishListItemOptional.get();

            //현재상품의 수량 중가
            wishListItem.changeCount(wishListItem.getCount() + wishListItemCount);
            wishListItemRepository.save(wishListItem);
        } else {
            try {
                // 상품이 없을 경우
                ProductResponseDto product = productServiceClient.findByIdStatusProduct(productId);

                // WishListItem 생성
                WishListItem newWishListItem = WishListItem.builder()
                        .productId(productId)
                        .memberEmail(email)
                        .wishList(wishList)
                        .count(wishListItemCount)
                        .productName(product.getProductName())
                        .productPrice(product.getProductPrice())
                        .build();

                wishListItemRepository.save(newWishListItem);
            }catch (FeignException e){
                if(e.status() == 400){
                    throw new CustomException("상품을 찾을 수 없습니다: " + productId);
                }else {
                    throw new CustomException("상품 서비스 호출 중 오류가 발생했습니다.");
                }
            }
        }
        //totalPrice변경
        totalPriceModify(wishList);

        return wishList(email);
    }


    @Override
    public WishListResponseDto deleteWishListItem(Long wishListItemId, String email) {
        // 삭제할 WishListItem을 조회
        Optional<WishListItem> optionalWishListItem = wishListItemRepository.findById(wishListItemId);

        // 항목이 존재하지 않으면 예외 발생
        if (!optionalWishListItem.isPresent()) {
            throw new CustomException("해당 위시리스트 항목을 찾을 수 없습니다.");
        }
        // item 삭제
        wishListItemRepository.deleteById(wishListItemId);
        
        // 수량변경
        //MemberResponseDto user = memberServiceClient.getUserByEmail(email);
        WishList wishList = wishListRepository.findByMemberEmail(email).orElseThrow(() -> new CustomException("위시리스트가 비어있습니다."));
        totalPriceModify(wishList);
        
        return wishList(email);
    }

    //WishListId를 통해 WishListItem찾기
    @Override
    public List<WishListItem> findAllWishListItem(Long wishListId){
        return wishListItemRepository.findByWishListId(wishListId);
    }

    @Override
    public void deleteId(Long wishListId) {
        // 주어진 wishListId를 기준으로 WishListItem 삭제
        Optional<WishList> wishListOptional = wishListRepository.findById(wishListId);

        if(wishListOptional.isPresent()){
            WishList wishList = wishListOptional.get();
            wishListItemRepository.deleteByWishListId(wishListId);
            wishList.changeTotalPrice(0L);
        }
    }


    // 위시리스트를 새로 생성하는 메서드
    private WishList createNewWishList(String email) {

        WishList newWishList = WishList.builder()
                .memberEmail(email)
                .build();

        return wishListRepository.save(newWishList);

    }

    private void totalPriceModify(WishList wishList){
        // totalPrice 계산
        List<WishListItem> wishListItems = wishListItemRepository.findByWishList(wishList);

        Long totalPrice = wishListItems.stream().mapToLong(
                wishListItem -> wishListItem.getProductPrice() * wishListItem.getCount()
        ).sum();

        wishList.changeTotalPrice(totalPrice);

    }

    public WishListResponseDto productServiceFallback(WishListItemDto wishListItemDto, String email, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        throw new CustomException("상품 서비스가 현재 사용 불가합니다. 나중에 다시 시도해주세요.");
    }

}
