package com.AuthService.auth.services;

import com.AuthService.auth.DTO.UserDTO;
import com.AuthService.auth.entities.TokenEntity;
import com.AuthService.auth.entities.UserEntity;
import com.AuthService.auth.repo.TokenRepo;
import com.AuthService.auth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new CustomUserDetails(user);
    }

    public void saveUser(UserEntity user){
        user.setPassword(user.getPassword());
        userRepo.save(user);
    }

    public String saveRefreshToken(String username, String refreshToken){
        TokenEntity token = new TokenEntity();
        token.setUsername(username);
        token.setToken(refreshToken);
        token.setExpiryDate(new Date(System.currentTimeMillis() + 60000));
        tokenRepo.save(token)   ;

        return  token.getToken();
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        TokenEntity token = tokenRepo.findByToken(refreshToken);
        if (token == null) {
            return false;
        }
        return !token.getExpiryDate().before(new Date());
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        TokenEntity token = tokenRepo.findByToken(refreshToken);
        return token != null ? token.getUsername() : null;
    }
}
