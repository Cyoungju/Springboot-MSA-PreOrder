package com.example.userservice.repository;

import com.example.userservice.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshRepository extends CrudRepository<RefreshToken, String> {
}
