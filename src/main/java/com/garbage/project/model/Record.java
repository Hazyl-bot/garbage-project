package com.garbage.project.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.garbage.project.util.GARBAGE_TYPE;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_record")
public class Record {
    @Id
    private String id;
    private String ownerId;
    private String garbageBinId;
    private GARBAGE_TYPE type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime gmtCreated;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime gmtModified;

    public Record() {
    }

    public Record(String id, String ownerId, String garbageBinId, GARBAGE_TYPE type) {
        this.id = id;
        this.ownerId = ownerId;
        this.garbageBinId = garbageBinId;
        this.type = type;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public String getGarbageBinId() {
        return garbageBinId;
    }

    public void setGarbageBinId(String garbageBinId) {
        this.garbageBinId = garbageBinId;
    }

    public GARBAGE_TYPE getType() {
        return type;
    }

    public void setType(GARBAGE_TYPE type) {
        this.type = type;
    }
}
