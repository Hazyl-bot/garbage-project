package com.garbage.project.param;

import com.garbage.project.util.GARBAGE_TYPE;

import java.time.LocalDateTime;

public class RecordInfo {
    private String location;
    private String ownerId;
    private GARBAGE_TYPE type;
    private LocalDateTime gmtCreated;


    public RecordInfo() {
    }

    public RecordInfo(String location, String ownerId, GARBAGE_TYPE type, LocalDateTime gmtCreated) {
        this.location = location;
        this.ownerId = ownerId;
        this.type = type;
        this.gmtCreated = gmtCreated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public GARBAGE_TYPE getType() {
        return type;
    }

    public void setType(GARBAGE_TYPE type) {
        this.type = type;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }
}
