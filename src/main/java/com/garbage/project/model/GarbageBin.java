package com.garbage.project.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.garbage.project.util.GARBAGE_TYPE;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "garbage_bin")
public class GarbageBin {
    @Id
    private String id;
    private String location;
    private GARBAGE_TYPE type;
    private Integer capacity;
    private Integer contain;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime lastClean;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected LocalDateTime gmtCreated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected LocalDateTime gmtModified;

    public GarbageBin(String id,String location, GARBAGE_TYPE type, int capacity, int contain, LocalDateTime lastClean, LocalDateTime gmtCreated, LocalDateTime gmtModified) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.capacity = capacity;
        this.contain = contain;
        this.lastClean = lastClean;
        this.gmtCreated = gmtCreated;
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public GarbageBin(String id,String location, GARBAGE_TYPE type) {
        this.id = id;
        this.location = location;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GarbageBin() {
    }

    public Integer getContain() {
        return contain;
    }

    public void setContain(Integer contain) {
        this.contain = contain;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GARBAGE_TYPE getType() {
        return type;
    }

    public void setType(GARBAGE_TYPE type) {
        this.type = type;
    }

    public LocalDateTime getLastClean() {
        return lastClean;
    }

    public void setLastClean(LocalDateTime lastClean) {
        this.lastClean = lastClean;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}
