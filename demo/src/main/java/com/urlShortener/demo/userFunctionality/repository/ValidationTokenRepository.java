package com.urlShortener.demo.userFunctionality.repository;

import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationTokenRepository extends JpaRepository<ValidationToken, Long> {
    boolean existsByToken(String code);

    ValidationToken findByToken(String code);

    void deleteByToken(String token);
}
