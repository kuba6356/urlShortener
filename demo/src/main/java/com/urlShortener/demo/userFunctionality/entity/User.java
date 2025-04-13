package com.urlShortener.demo.userFunctionality.entity;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String username;
    private String passwordHash;
    private boolean activated = false;
    @OneToMany(
            cascade = CascadeType.REMOVE
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userId"
    )
    private List<Url> url = new ArrayList<>();

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }
    public User() {
    }



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void changeToActive(){
        this.activated = true;
    }

    public boolean isActivated() {
        return activated;
    }

    public List<Url> getUrl() {
        return url;
    }

    public void setUrl(List<Url> url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", activated=" + activated +
                ", url=" + url +
                '}';
    }

    public void addUrl(Url url){
        this.url.add(url);
    }

}
