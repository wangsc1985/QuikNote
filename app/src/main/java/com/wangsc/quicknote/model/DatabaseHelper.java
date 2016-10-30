package com.wangsc.quicknote.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 14;
    private static final String DATABASE_NAME = "quicknote.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO 创建数据库后，对数据库的操作
        db.execSQL("create table if not exists note("
                + "id TEXT PRIMARY KEY,"
                + "title TEXT,"
                + "content TEXT,"
                + "createTime INTEGER,"
                + "lastModifyTime INTEGER,"
                + "status INTEGER,"
                + "serial INTEGER,"
                + "summaryDetail INTEGER,"
                + "userId TEXT)");

        db.execSQL("create table if not exists category("
                + "id TEXT PRIMARY KEY,"
                + "parentId TEXT,"
                + "value TEXT,"
                + "userId TEXT)");

        db.execSQL("create table if not exists tag("
                + "id TEXT PRIMARY KEY,"
                + "value TEXT,"
                + "userId TEXT)");

        db.execSQL("create table if not exists user("
                + "id TEXT PRIMARY KEY,"
                + "userName TEXT ,"
                + "password TEXT ,"
                + "email TEXT,"
                + "phone TEXT,"
                + "userId TEXT)");
        db.execSQL("create table if not exists setting("
                + "id TEXT PRIMARY KEY,"
                + "key TEXT ,"
                + "value TEXT ,"
                + "userId TEXT)");
            db.execSQL("create table if not exists log("
                    + "id TEXT PRIMARY KEY,"
                    + "dateTime INTEGER ,"
                    + "sql TEXT ,"
                    + "userId TEXT,"
                    + "operate INTEGER,"
                    + "sqlValues TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 更改数据库版本的操作
        if (oldVersion == 11 && newVersion == 12) {
            db.execSQL("create table if not exists log("
                    + "id TEXT PRIMARY KEY,"
                    + "dateTime INTEGER ,"
                    + "sql TEXT ,"
                    + "userId TEXT)");
            db.execSQL("ALTER TABLE user ADD phone TEXT");
        }
        if (oldVersion == 12 && newVersion == 13) {
            db.execSQL("ALTER TABLE log ADD operate INTEGER");
            db.execSQL("ALTER TABLE log ADD sqlValues TEXT");
        }
        if (oldVersion == 13 && newVersion == 14) {
            db.execSQL("ALTER TABLE note RENAME TO note_temp;");
            db.execSQL("create table if not exists note("
                    + "id TEXT PRIMARY KEY,"
                    + "title TEXT,"
                    + "content TEXT,"
                    + "createTime INTEGER,"
                    + "lastModifyTime INTEGER,"
                    + "status INTEGER,"
                    + "serial INTEGER,"
                    + "summaryDetail INTEGER,"
                    + "userId TEXT)");
            db.execSQL("insert into note(id,title,content,createTime,lastModifyTime,status,serial,summaryDetail,userId) select (id,title,content,createTime,lastModifyTime,status,serial,summaryDetial,userId) from note_temp;");
            db.execSQL("drop table note_temp;");
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执行
    }


}
