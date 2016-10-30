package com.wangsc.quicknote.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/12.
 */
public class Category implements Serializable {
    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        value = value;
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
    private UUID parentId;
    private String value;
    private UUID userId;

    public Category(UUID userId) {
        id = UUID.randomUUID();
        this.userId =userId;
        parentId = null;
    }

    public Category(UUID userId, UUID parentId) {
        this(userId);
        this.parentId = parentId;
    }
}
