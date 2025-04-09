package com.urlShortener.demo.urlFunctionality.repository;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByLongLinkAndCreatedAt(String longUrl, LocalTime createdAt);
}
