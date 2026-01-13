package models;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Link {

    //    private static final long serialVersionUID = 1L;
    private Long id;
    private String url;
    private String shortUrl;
    private User user;
    private int linkForwardLimit;
    private LocalDateTime createTime;
    private Duration lifeTime;

    public Link() {
    }

    public Link(String url, User user, int linkForwardLimit, Duration lifeTime) {
        id = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        this.url = url;
        this.user = user;
        this.linkForwardLimit = linkForwardLimit;
        this.createTime = LocalDateTime.now();
        this.lifeTime = lifeTime;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Duration getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Duration lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public int getLinkForwardLimit() {
        return linkForwardLimit;
    }

    public void setLinkForwardLimit(int linkForwardLimit) {
        this.linkForwardLimit = linkForwardLimit;
    }

    public LocalDateTime getExpireTime() {
        return createTime.plus(lifeTime);
    }
}
