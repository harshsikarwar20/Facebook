package com.example.Facebook.repository;

import com.example.Facebook.models.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITokenRepository extends JpaRepository<AuthenticationToken , Long> {
    AuthenticationToken findFirstByToken(String token);

}
