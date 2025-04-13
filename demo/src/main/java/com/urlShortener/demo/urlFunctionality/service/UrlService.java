package com.urlShortener.demo.urlFunctionality.service;


import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    String urlCreateService(String longUrl, User loggedInUser);

    Url findById(Long id);

    void deleteAllUrlAnalyticsData(Long id);

    String updateUrl(Long id, String newLongUrl, User user);

    String deleteUrl(Long id, User loggedInUser);

    String redirectClick(Long id, HttpServletRequest request);

    String encode(Long id);

    Url getUrl(Long id, User loggedInUser);
}
