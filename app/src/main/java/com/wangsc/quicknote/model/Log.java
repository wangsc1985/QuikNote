package com.wangsc.quicknote.model;

import com.wangsc.quicknote._enum.Operate;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/23.
 */
public class Log {

    private UUID id;
    private Calendar dateTime;
    private String sql;
    private UUID userId;
    private Operate operate;
    private String sqlValues;

    public Log(UUID userId){
        this.id=UUID.randomUUID();
        this.dateTime=Calendar.getInstance();
        this.userId=userId;
    }
    public Log(Operate operateType,String sql,String sqlValues,UUID userId){
        this(userId);
        this.operate = operateType;
        this.sql=sql;
        this.sqlValues = sqlValues;
    }

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

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    public String getSqlValues() {
        return sqlValues;
    }

    public void setSqlValues(String sqlValues) {
        this.sqlValues = sqlValues;
    }

}
