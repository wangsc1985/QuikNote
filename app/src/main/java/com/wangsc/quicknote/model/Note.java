package com.wangsc.quicknote.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/12.
 */
public class Note implements Serializable {
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }
    public void setLastModifyTime(Calendar lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public Calendar getCreateTime() {
        return createTime;
    }
    public Calendar getLastModifyTime() {
        return lastModifyTime;
    }
    /**
     * 状态 1正常，0删除，2隐藏
     * @return
     */
    public int getStatus() {
        return status;
    }
    /**
     * 状态 1正常，0删除，2隐藏
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }
    /**
     * 排序序号
     * @return
     */
    public int getSerial() {
        return serial;
    }
    /**
     * 排序序号
     * @param serial
     */
    public void setSerial(int serial) {
        this.serial = serial;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public boolean isListAllContent() {
        return listAllContent;
    }
    public void setListAllContent(boolean listAllContent) {
        this.listAllContent = listAllContent;
    }

    private UUID id;
    private String title;
    private String content;
    private Calendar createTime;
    private Calendar lastModifyTime;
    private int status;
    private int serial;
    private boolean listAllContent;
    private UUID userId;

    public Note(UUID userId){
        id = UUID.randomUUID();
        this.userId = userId;
        createTime = lastModifyTime =Calendar.getInstance();
        status=1;
        title = "";
        content="";
        listAllContent =false;
    }

    public Note(String title,String content,UUID userId){
        this(userId);
        this.title = title;
        this.content = content;
    }
}
