package com.urlShortener.demo.urlFunctionality.service;


import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    String urlCreateService(String longUrl, User loggedInUser);

    Url findById(Long id);

    void deleteAllUrlAnalyticsData(Long id);

    void updateUrl(Long id, String newLongUrl);

    void deleteUrl(java.lang.Long id);

    String redirectClick(Long id, HttpServletRequest request);
}
