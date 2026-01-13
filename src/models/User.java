package models;


import java.util.UUID;

public class User {
    String uuid;
    long uuid_long;

    public User() {
        this.uuid = UUID.randomUUID().toString();
        uuid_long = Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    public String getUuid() {
        return uuid;
    }

    public long getUuid_long() {
        return uuid_long;
    }
}