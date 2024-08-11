package com.example.ecommerceproject.address.repository;

import com.example.ecommerceproject.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
