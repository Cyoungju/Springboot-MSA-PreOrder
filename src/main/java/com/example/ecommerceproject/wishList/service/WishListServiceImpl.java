package com.example.ecommerceproject.wishList.service;

import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.service.MemberService;
import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.service.ProductService;
import com.example.ecommerceproject.wishList.dto.WishListItemDto;
import com.example.ecommerceproject.wishList.dto.WishListItemListDto;
import com.example.ecommerceproject.wishList.dto.WishListResponseDto;
import com.example.ecommerceproject.wishList.entity.WishList;
import com.example.ecommerceproject.wishList.entity.WishListItem;
import com.example.ecommerceproject.wishList.repositopry.WishListItemRepository;
import com.example.ecommerceproject.wishList.repositopry.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional
@Service
public class WishListServiceImpl implements WishListService{

    private final WishListItemRepository wishListItemRepository;
    private final WishListRepository wishListRepository;
    private final MemberService memberService;
    private final ProductService productService;


    // 전체 상품 조회
    @Override
    public WishListResponseDto wishList(String email) {
        Long memberId = memberService.memberByEmail(email);
        List<WishListItem> items = wishListItemRepository.findByWishListMemberId(memberId); // ID로 아이템 조회


        WishList wishList = wishListRepository.findByMember_Email(email)
                .orElseGet(() -> createNewWishList(email));

        Long totalPrice = wishList.getTotalPrice();
        List<WishListItemListDto> itemDtos= items.stream()
                .map(item -> new WishListItemListDto(item.getId(), item.getCount(), item.getProduct().getName(), item.getProduct().getPrice()))
                .collect(Collectors.toList());

        return new WishListResponseDto(itemDtos, totalPrice);
    }

    @Override
    public WishListResponseDto addWishList(WishListItemDto wishListItemDto, String email) {
        // 상품 추가
        Long wishListItemCount = wishListItemDto.getCount();

        Long productId = wishListItemDto.getProductId();

        // 이메일을 통해 WishList를 찾기
        WishList wishList = findByMemberEmail(email);

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
            // 상품이 없을 경우
            Product product = productService.findByIdProduct(productId);

            // WishListItem 생성
            WishListItem newWishListItem = WishListItem.builder()
                    .product(product)
                    .wishList(wishList)
                    .count(wishListItemCount)
                    .build();

            wishListItemRepository.save(newWishListItem);
        }
        //totalPrice변경
        totalPriceModify(wishList);
        
        return wishList(email);
    }


    // 위시리스트를 새로 생성하는 메서드
    private WishList createNewWishList(String email) {
        Member member = memberService.getMemberByEmail(email);

        WishList newWishList = WishList.builder()
                .member(member)
                .build();

        return wishListRepository.save(newWishList);
    }

    private void totalPriceModify(WishList wishList){
        // totalPrice 계산
        List<WishListItem> wishListItems = wishListItemRepository.findByWishList(wishList);
        Long totalPrice = wishListItems.stream().mapToLong(
                wishListItem -> wishListItem.getProduct().getPrice() * wishListItem.getCount()
        ).sum();

        wishList.changeTotalPrice(totalPrice);
    }

    @Override
    public WishListResponseDto deleteWishListItem(Long wishListItemId, String email) {
        // 삭제할 WishListItem을 조회
        Optional<WishListItem> optionalWishListItem = wishListItemRepository.findById(wishListItemId);

        // 항목이 존재하지 않으면 예외 발생
        if (!optionalWishListItem.isPresent()) {
            throw new CustomException("해당 위시리스트 항목을 찾을 수 없습니다.");
        }

        WishList wishList = wishListRepository.findByMember_Email(email).orElseThrow(() -> new CustomException("위시리스트가 비어있습니다."));

        //totalPrice변경
        totalPriceModify(wishList);

        wishListItemRepository.deleteById(wishListItemId);
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
        wishListItemRepository.deleteByWishListId(wishListId);
    }


    // 이메일을 통해 WishList를 찾기
    @Override
    public WishList findByMemberEmail(String email){
        WishList wishList = wishListRepository.findByMember_Email(email)
                .orElseGet(() -> createNewWishList(email));
        return wishList;
    }

}
