package com.urlShortener.demo.urlFunctionality.service;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.entity.UrlAnalytics;
import com.urlShortener.demo.urlFunctionality.repository.UrlAnalyticsRepository;
import com.urlShortener.demo.urlFunctionality.repository.UrlRepository;
import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UrlAnalyticsRepository urlAnalyticsRepository;


    @Override
    public String urlCreateService(String longUrl, User loggedInUser) {
        Url url = new Url(longUrl, loggedInUser);
        urlRepository.save(url);
        //change it to look for long link + user
        url = urlRepository.findByLongLinkAndCreatedAt(longUrl, url.getCreatedAt());
        url.setShortLink(url.encode(url.getId()));
        urlRepository.save(url);
        return "Url created successfully";
    }

    public Url findById(Long id) {
        return urlRepository.findById(id).get();
    }

    @Override
    public void deleteAllUrlAnalyticsData(Long id) {
        urlAnalyticsRepository.deleteAll(urlAnalyticsRepository.findAllByUrl(urlRepository.findById(id).get()));
    }

    @Override
    public void updateUrl(Long id, String newLongUrl) {
        deleteAllUrlAnalyticsData(id);
        Url url = findById(id);
        url.setLongLink(newLongUrl);
        url.setClickCounter(0);
        urlRepository.save(url);
    }

    @Override
    public void deleteUrl(Long id) {
        Url url = findById(id);
        urlAnalyticsRepository.findAllByUrl(Optional.ofNullable(url).get());
        urlRepository.delete(url);
    }

    @Override
    public String redirectClick(Long id, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        Url url = findById(id);
        url.setClickCounter(url.getClickCounter() +1);
        UrlAnalytics urlAnalytics = new UrlAnalytics(ipAddress, LocalTime.now().toString(), TimeZone.getDefault().getID(), url);
        urlAnalyticsRepository.save(urlAnalytics);
        return url.getLongLink();
    }
}
