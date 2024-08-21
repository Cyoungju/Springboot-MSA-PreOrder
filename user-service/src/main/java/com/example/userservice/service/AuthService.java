package com.example.userservice.service;


import com.example.userservice.entity.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Service
public interface AuthService {

    ResponseEntity<?> reissue(HttpServletResponse response, HttpServletRequest request) throws IOException;
}
