package com.urlShortener.demo.userFunctionality.entity;

import jakarta.persistence.*;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class PasswordValidationToken {
    private static final int expirationTimeInMinutes = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long passwordValidationTokenId;
    private String token;
    private Date expirationDate;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "userID"
    )
    private User user;

    public User getUser() {
        return user;
    }

    public PasswordValidationToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.expirationDate = calculateExpirationDate(expirationTimeInMinutes) ;
        this.user = user;
    }
    public PasswordValidationToken(){}


    public Long getValidationTokenId() {
        return passwordValidationTokenId;
    }

    public void setValidationTokenId(Long validationTokenId) {
        this.passwordValidationTokenId = validationTokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date calculateExpirationDate(int expirationTimeInMinutes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTimeInMinutes);
        return new Date(calendar.getTime().getTime());
    }

    @Override
    public String toString() {
        return "ValidationToken{" +
                "id=" + passwordValidationTokenId +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }


}
