package com.example.ecommerceproject.member.repository;

import com.example.ecommerceproject.member.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
