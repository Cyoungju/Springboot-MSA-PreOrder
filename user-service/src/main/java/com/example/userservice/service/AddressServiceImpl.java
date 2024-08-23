package com.example.userservice.service;

import com.example.userservice.core.exception.CustomException;
import com.example.userservice.core.utils.EncryptionUtil;
import com.example.userservice.dto.AddressAddDto;
import com.example.userservice.dto.AddressResponseDto;
import com.example.userservice.entity.Address;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.AddressRepository;
import com.example.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;

    private final MemberRepository memberRepository;

    private final EncryptionUtil encryptionUtil;

    //배송지 추가
    @Transactional
    public void addAddress(AddressAddDto addressAddDto, String email){

        Member member = memberRepository.findByEmail(email);
        Long memberId = member.getId();

        // 기존에 추가된 배송지가 없을 경우
        if (addressAddDto.isDefaultAdr()) {
            // 기존의 모든 기본 배송지를 false로 업데이트
            addressRepository.updateDefaultAdrToFalse(memberId);
        }


        Address address = Address.builder()
                .addressName(addressAddDto.getAddressName())
                .address(encryptionUtil.encrypt(addressAddDto.getAddress()))
                .detailAdr(encryptionUtil.encrypt(addressAddDto.getDetailAdr())) // 상세 주소 설정
                .defaultAdr(addressAddDto.isDefaultAdr())
                .phone(encryptionUtil.encrypt(addressAddDto.getPhone()))
                .member(member)
                .build();

        // 주소 저장
        addressRepository.save(address);
    }

    @Override
    public AddressResponseDto getDefaultAddress(String email) {
        Address address = addressRepository.findByMemberEmailAndDefaultAdrTrue(email)
                .orElseThrow(() -> new CustomException("기본 배송지를 찾을 수 없습니다."));
        return new AddressResponseDto(address);
    }

    @Override
    public AddressResponseDto getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new CustomException("해당 배송지를 찾을 수 없습니다."));
        return new AddressResponseDto(address);
    }
}
