package com.urlShortener.demo.urlFunctionality.service;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.entity.UrlAnalytics;
import com.urlShortener.demo.urlFunctionality.repository.UrlAnalyticsRepository;
import com.urlShortener.demo.urlFunctionality.repository.UrlRepository;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@Service
public class UrlServiceImpl implements UrlService {
    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UrlAnalyticsRepository urlAnalyticsRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public String urlCreateService(String longUrl, User loggedInUser) {
        try {
            User user = userRepository.findByUserId(loggedInUser.getUserId());
            Url url = new Url(longUrl, user);
            urlRepository.save(url);
            //change it to look for long link + user
            url = urlRepository.findByLongLinkAndCreatedAt(longUrl, url.getCreatedAt());
            url.setShortLink(encode(url.getUrlId()));
            urlRepository.save(url);
            user.addUrl(url);
            userRepository.save(user);
            return "Url created successfully";
        } catch (Exception e) {
            throw new RuntimeException();
            }
        }

    public Url findById(Long id) {
        return urlRepository.findById(id).get();
    }


    @Override
    public Url getUrl(Long id, User loggedInUser) 
        if(findById(id).getUserId() == loggedInUser.getUserId()){
            return findById(id);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteAllUrlAnalyticsData(Long id) {
        try{
        Url url = urlRepository.findById(id).get();
        urlAnalyticsRepository.deleteAllInBatch(url.getUrlAnalytics());
        urlRepository.save(url);
    }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Transactional
    @Override
    public String updateUrl(Long id, String newLongUrl, User user) {
        try {
            Url url = findById(id);

            if (!user.getUserId().equals(url.getUserId())) {
                return "Invalid User";
            }
            deleteAllUrlAnalyticsData(id);
            url.setLongLink(newLongUrl);
            url.setClickCounter(0);
            urlRepository.save(url);
            return "Url has been Updated";
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Transactional
    @Override
    public String deleteUrl(Long id, User loggedInUser) {
        try {
            Url url = findById(id);
            if (!user.getUserId().equals(url.getUserId())) {
                return "Invalid User";
            }
            deleteAllUrlAnalyticsData(id);
            urlRepository.deleteById(url.getUrlId());
            return "url deleted succesfully";
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Transactional
    @Override
    public String redirectClick(Long id, HttpServletRequest request) {
        try {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            Url url = findById(id);
            url.setClickCounter(url.getClickCounter() + 1);
            UrlAnalytics urlAnalytics = new UrlAnalytics(ipAddress, LocalDateTime.now(), request.getLocale().toString());
            urlAnalyticsRepository.save(urlAnalytics);
            url.addUrlAnalytics(urlAnalytics);
            urlRepository.save(url);
            User user = userRepository.findByUserId(url.getUserId());
            userRepository.save(user);
            return url.getLongLink();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public String encode(Long id) {
        try {
            final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            StringBuilder shortUrl = new StringBuilder();
            while (id > 0) {
                shortUrl.append(CHARACTERS.charAt((int) (id % 62)));
                id /= 62;
            }
            return shortUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
