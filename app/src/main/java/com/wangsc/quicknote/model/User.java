package com.wangsc.quicknote.model;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class User {

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private UUID id;
    private String userName;
    private String password;
    private String email;

    public User(){
        id = UUID.randomUUID();
    }
}
