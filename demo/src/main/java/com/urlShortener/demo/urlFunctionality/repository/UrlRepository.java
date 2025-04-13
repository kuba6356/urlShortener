package com.urlShortener.demo.urlFunctionality.repository;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByLongLinkAndCreatedAt(String longUrl, LocalDateTime createdAt);
}
