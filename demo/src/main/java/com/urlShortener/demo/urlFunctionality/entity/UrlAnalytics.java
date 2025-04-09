package com.urlShortener.demo.urlFunctionality.entity;

import jakarta.persistence.*;

@Entity
public class UrlAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ipAddress;
    private String time;
    private String location;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "Url_id",
            referencedColumnName = "id"
    )
    private Url url;

    public UrlAnalytics(String ipAddress, String time, String location, Url url) {
        this.ipAddress = ipAddress;
        this.time = time;
        this.location = location;
        this.url = url;
    }
    public UrlAnalytics(){}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UrlAnalytics{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", url=" + url +
                '}';
    }

}
