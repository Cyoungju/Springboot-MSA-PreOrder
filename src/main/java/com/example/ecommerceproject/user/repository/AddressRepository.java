package com.example.ecommerceproject.user.repository;

import com.example.ecommerceproject.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
