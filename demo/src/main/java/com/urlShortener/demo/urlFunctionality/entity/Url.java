package com.urlShortener.demo.urlFunctionality.entity;

import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long urlId;
    private String longLink;
    private String shortLink;
    private Integer clickCounter;
    private LocalDateTime createdAt;

    public List<UrlAnalytics> getUrlAnalytics() {
        return urlAnalytics;
    }

    @OneToMany(
            cascade = CascadeType.REMOVE
    )
    @JoinColumn(
            name = "url_id",
            referencedColumnName = "urlId"
    )
    private List<UrlAnalytics> urlAnalytics ;
    @ManyToOne(
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    private User user;

    public Url(String longLink, User user) {
        this.longLink = longLink;
        this.clickCounter = 0;
        this.createdAt = LocalDateTime.now();
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }


    public void setUrlAnalytics(List<UrlAnalytics> urlAnalytics) {
        this.urlAnalytics = urlAnalytics;
    }

    public void addUrlAnalytics(UrlAnalytics urlAnalytics) {
        List<UrlAnalytics> x = new ArrayList<>();
        x = getUrlAnalytics();
        x.add(urlAnalytics);
        setUrlAnalytics(x);
    }

    public Url(){}

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public String getLongLink() {
        return longLink;
    }

    public void setLongLink(String longLink) {
        this.longLink = longLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public Integer getClickCounter() {
        return clickCounter;
    }

    public void setClickCounter(Integer clickCounter) {
        this.clickCounter = clickCounter;
    }

    @Override
    public String toString() {
        return "Url{" +
                "urlId=" + urlId +
                ", longLink='" + longLink + '\'' +
                ", shortLink='" + shortLink + '\'' +
                ", clickCounter=" + clickCounter +
                ", createdAt=" + createdAt +
                ", urlAnalytics=" + urlAnalytics +
                '}';
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
