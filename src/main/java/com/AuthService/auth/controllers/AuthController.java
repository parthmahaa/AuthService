package com.AuthService.auth.controllers;

import com.AuthService.auth.DTO.UserDTO;
import com.AuthService.auth.config.ModelMapper;
import com.AuthService.auth.entities.UserEntity;
import com.AuthService.auth.services.JWTService;
import com.AuthService.auth.services.RefreshTokenService;
import com.AuthService.auth.services.UserService;
import com.AuthService.auth.utilities.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final com.AuthService.auth.services.JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JWTService jwtService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, UserService userService, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<ResponseWrapper<Map<String,String>>> signUp(@RequestBody UserEntity user){
        try {
            // Save the user first
            userService.saveUser(user);

            // Now generate tokens
            String jwtToken = jwtService.GenerateToken(user.getUsername());
            String refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            Map<String,String> tokens = new HashMap<>();
            tokens.put("jwtToken" ,jwtToken);
            tokens.put("refreshToken" ,refreshToken);

            System.out.println(tokens);

            ResponseWrapper<Map<String, String>> responseWrapper = new ResponseWrapper<>(
                    LocalDateTime.now(),
                    HttpStatus.CREATED.value(),
                    "SignUp Successful",
                    tokens,
                    false
            );
            System.out.println(responseWrapper);
            return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}