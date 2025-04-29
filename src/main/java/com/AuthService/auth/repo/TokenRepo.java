package com.AuthService.auth.repo;

import com.AuthService.auth.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<TokenEntity,Long> {
}
