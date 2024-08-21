package com.example.userservice.repository;

import com.example.userservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.defaultAdr = false WHERE a.member.id = :memberId")
    void updateDefaultAdrToFalse(@Param("memberId") Long memberId);

    Optional<Address> findByMemberIdAndDefaultAdrTrue(Long memberId);

    Optional<Address> findByMemberId(Long memberId);
}
