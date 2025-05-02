package com.AuthService.auth.controllers;

import com.AuthService.auth.DTO.UserDTO;
import com.AuthService.auth.config.ModelMapper;
import com.AuthService.auth.entities.TokenEntity;
import com.AuthService.auth.entities.UserEntity;
import com.AuthService.auth.repo.TokenRepo;
import com.AuthService.auth.services.JWTService;
import com.AuthService.auth.services.RefreshTokenService;
import com.AuthService.auth.services.UserService;
import com.AuthService.auth.utilities.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.webauthn.api.AuthenticatorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final com.AuthService.auth.services.JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private UserService userService;
    private final AuthenticationManager authenticationManager;


    private final TokenRepo tokenRepo;

    public AuthController(JWTService jwtService,TokenRepo tokenRepo, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, UserService userService, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.tokenRepo = tokenRepo;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping(path = "")
    public String getRequest(){
        return "A simple get request called";
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

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseWrapper<?>> login(@RequestBody UserEntity user){

        try{

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            String jwtToken = jwtService.GenerateToken(user.getUsername());
            String refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            Map<String,String> tokens = new HashMap<>();
            tokens.put("jwtToken" ,jwtToken);
            tokens.put("refreshToken" ,refreshToken);

            ResponseWrapper<Map<String, String>> responseWrapper = new ResponseWrapper<>(
                    LocalDateTime.now(),
                    HttpStatus.CREATED.value(),
                    "Login Successful",
                    tokens,
                    false
            );
            return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
        }catch(Exception e){
            System.out.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Login Failed");
        }

    }

    @PostMapping(path = "/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        System.out.println("Received refreshToken request with token: " + refreshToken);
        TokenEntity tokenEntity = tokenRepo.findByToken(refreshToken);

        if (tokenEntity == null) {
            System.out.println("Refresh token not found: " + refreshToken);
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Refresh token not found",
                    null,
                    true
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (tokenEntity.getExpiryDate().before(new Date())) {
            System.out.println("Refresh token expired: " + refreshToken);
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Refresh token has expired. Please log in again.",
                    null,
                    true
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String username = tokenEntity.getUsername();
        String newJwtToken = jwtService.GenerateToken(username);
        System.out.println("Generated new JWT token for user: " + username);
        ResponseWrapper<AuthResponse> successResponse = new ResponseWrapper<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "New JWT token generated successfully",
                new AuthResponse(newJwtToken, refreshToken),
                false
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Refresh token is required",
                    null,
                    true
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        TokenEntity tokenEntity = tokenRepo.findByToken(refreshToken);
        if (tokenEntity != null) {
            tokenRepo.delete(tokenEntity);
            System.out.println("Refresh Token Deleted");
        }

        ResponseWrapper<String> successResponse = new ResponseWrapper<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Logout successful",
                null,
                false
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}

record AuthResponse(String jwtToken,String refreshToken){}