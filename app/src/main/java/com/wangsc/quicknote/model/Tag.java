package com.wangsc.quicknote.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/12.
 */
public class Tag implements Serializable {
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public UUID getId() {
        return id;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    private UUID id;
    private String value;
    private UUID userId;

    public Tag(UUID userId){
        id = UUID.randomUUID();
        this.userId = userId;
    }
}
