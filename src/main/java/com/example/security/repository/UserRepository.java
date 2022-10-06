package com.example.security.repository;

import com.example.security.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
}
