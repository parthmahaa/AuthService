package com.AuthService.auth.repo;

import com.AuthService.auth.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRoleEntity, Long> {
    UserRoleEntity findByName(String name);
}
