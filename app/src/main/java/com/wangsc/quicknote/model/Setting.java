package com.wangsc.quicknote.model;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class Setting {

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public Setting(UUID userId){
        id=UUID.randomUUID();
        this.userId = userId;
    }
    private UUID id;
    private String key;
    private String value;
    private UUID userId;
}
