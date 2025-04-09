package com.urlShortener.demo.urlFunctionality.entity;

import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String longLink;
    private String shortLink;
    private Integer clickCounter;
    private LocalTime createdAt;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

    public Url(String longLink, User user) {
        this.longLink = longLink;
        this.clickCounter = 0;
        this.createdAt = LocalTime.now();
        this.user = user;
    }
    public Url(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String encode(Long id){
        final String Characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder shortUrl = new StringBuilder();
        while(id > 0){
            shortUrl.append(Characters.charAt((int) (id % 62)));
            id/= 62;
        }
        return shortUrl.toString();
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", longLink='" + longLink + '\'' +
                ", shortLink='" + shortLink + '\'' +
                ", clickCounter=" + clickCounter +
                ", createdAt=" + createdAt +
                ", user=" + user +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalTime createdAt) {
        this.createdAt = createdAt;
    }
}
