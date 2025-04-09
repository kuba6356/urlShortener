package com.urlShortener.demo.urlFunctionality.repository;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.entity.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlAnalyticsRepository extends JpaRepository<UrlAnalytics, Long> {
    Iterable<? extends UrlAnalytics> findAllByUrl(Url url);
}
