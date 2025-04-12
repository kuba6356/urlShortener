package com.urlShortener.demo.userFunctionality.repository;

import com.urlShortener.demo.userFunctionality.entity.PasswordValidationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordValidationTokenRepository extends JpaRepository<PasswordValidationToken, Long> {
    boolean existsByToken(String code);

    PasswordValidationToken findByToken(String code);

    void deleteByToken(String code);
}
