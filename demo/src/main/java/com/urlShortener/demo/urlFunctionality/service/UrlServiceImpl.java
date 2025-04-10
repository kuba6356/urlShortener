package com.urlShortener.demo.urlFunctionality.service;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.entity.UrlAnalytics;
import com.urlShortener.demo.urlFunctionality.repository.UrlAnalyticsRepository;
import com.urlShortener.demo.urlFunctionality.repository.UrlRepository;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collections;
import java.util.TimeZone;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UrlAnalyticsRepository urlAnalyticsRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public String urlCreateService(String longUrl, User loggedInUser) {
        User user = userRepository.findByUserId(loggedInUser.getUserId());
        Url url = new Url(longUrl, user);
        urlRepository.save(url);
        //change it to look for long link + user
        url = urlRepository.findByLongLinkAndCreatedAt(longUrl, url.getCreatedAt());
        url.setShortLink(url.encode(url.getUrlId()));
        urlRepository.save(url);
        user.addUrl(url);
        userRepository.save(user);
        return "Url created successfully";
    }

    public Url findById(Long id) {
        return urlRepository.findById(id).get();
    }

    @Override
    public void deleteAllUrlAnalyticsData(Long id) {
        Url url = urlRepository.findById(id).get();
        urlAnalyticsRepository.deleteAllInBatch(url.getUrlAnalytics());
        url.setUrlAnalytics(null);
        urlRepository.save(url);
    }

    @Override
    public String updateUrl(Long id, String newLongUrl, User user) {
        Url url = findById(id);

        if(userRepository.findByEmail(user.getEmail()) != userRepository.findByUrl(url)){
            return "Invalid User";
        }
        deleteAllUrlAnalyticsData(id);
        url.setLongLink(newLongUrl);
        url.setClickCounter(0);
        urlRepository.save(url);
        return "Url has been created";
    }

    @Override
    public String deleteUrl(Long id, User loggedInUser) {
        Url url = findById(id);
        if(userRepository.findByUrl(url) != userRepository.findByUserId(loggedInUser.getUserId())){
            return "Invalid User";
        }
        deleteAllUrlAnalyticsData(id);
        urlRepository.deleteById(url.getUrlId());
        return "url deleted succesfully";
    }

    @Override
    public String redirectClick(Long id, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        Url url = findById(id);
        url.setClickCounter(url.getClickCounter() +1);
        UrlAnalytics urlAnalytics = new UrlAnalytics(ipAddress, LocalTime.now().toString(), TimeZone.getDefault().getID());
        urlAnalyticsRepository.save(urlAnalytics);
        url.addUrlAnalytics(urlAnalytics);
        urlRepository.save(url);
        User user = userRepository.findByUserId(url.getUserId());
        user.addUrl(url);
        userRepository.save(user);
        return url.getLongLink();
    }
}
