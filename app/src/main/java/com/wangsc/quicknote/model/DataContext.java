package com.wangsc.quicknote.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.wangsc.quicknote._enum.Operate;
import com.wangsc.quicknote.helper.StringHelper;
import com.wangsc.quicknote.helper._Session;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;
    private Context context = null;

    public DataContext(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public Note getNote(UUID noteId) {
        Note note = null;
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("note", null, "id=?", new String[]{noteId.toString()}, null, null, null);
            if (cursor.moveToNext()) {
                note = new Note(UUID.fromString(cursor.getString(8)));
                note.setId(UUID.fromString(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                Calendar createTime = Calendar.getInstance();
                createTime.setTimeInMillis(cursor.getLong(3));
                note.setCreateTime(createTime);
                Calendar lastModifyTime = Calendar.getInstance();
                lastModifyTime.setTimeInMillis(cursor.getLong(4));
                note.setLastModifyTime(lastModifyTime);
                note.setStatus(cursor.getInt(5));
                note.setSerial(cursor.getInt(6));
                note.setListAllContent(cursor.getInt(7) == 0 ? false : true);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
        return note;
    }

    public List<Note> getNotes(int status, UUID userId) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("note", null, "userId=? AND status=?", new String[]{userId.toString(), status + ""}, null, null, "createTime ASC");
            while (cursor.moveToNext()) {
                Note note = new Note(UUID.fromString(cursor.getString(8)));
                note.setId(UUID.fromString(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                Calendar createTime = Calendar.getInstance();
                createTime.setTimeInMillis(cursor.getLong(3));
                note.setCreateTime(createTime);
                Calendar lastModifyTime = Calendar.getInstance();
                lastModifyTime.setTimeInMillis(cursor.getLong(4));
                note.setLastModifyTime(lastModifyTime);
                note.setStatus(cursor.getInt(5));
                note.setSerial(cursor.getInt(6));
                note.setListAllContent(cursor.getInt(7) == 0 ? false : true);
                notes.add(note);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
        return notes;
    }

    public void addNote(Note note) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", note.getId().toString());
            values.put("title", note.getTitle());
            values.put("content", note.getContent());
            values.put("createTime", note.getCreateTime().getTimeInMillis());
            values.put("lastModifyTime", note.getLastModifyTime().getTimeInMillis());
            values.put("status", note.getStatus());
            values.put("serial", note.getSerial());
            values.put("summaryDetail", note.isListAllContent());
            values.put("userId", note.getUserId().toString());

            db.insert("note", "id", values);
//            String sql = "INSERT INTO note (id,title,content,createTime,lastModifyTime,status,serial,summaryDetail,userId) " +
//                    "VALUES(@id,@title,@content,@createTime,@lastModifyTime,@status,@serial,@summaryDetail,@userId)";
            String sql = "INSERT INTO note (id,title,content,createTime,lastModifyTime,status,serial,summaryDetail,userId) " +
                    "VALUES(?,?,?,?,?,?,?,?,?)";
            addLog(new Log(Operate.ADD_NOTE, sql, note.getId().toString(), note.getUserId()), db);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void addNotes(List<Note> notes) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            for (Note note : notes) {
                ContentValues values = new ContentValues();
                values.put("id", note.getId().toString());
                values.put("title", note.getTitle());
                values.put("content", note.getContent());
                values.put("createTime", note.getCreateTime().getTimeInMillis());
                values.put("lastModifyTime", note.getLastModifyTime().getTimeInMillis());
                values.put("status", note.getStatus());
                values.put("serial", note.getSerial());
                values.put("summaryDetail", note.isListAllContent());
                values.put("userId", note.getUserId().toString());
                db.insert("note", "id", values);

                db.insert("note", "id", values);
//                String sql = "INSERT INTO note (id,title,content,createTime,lastModifyTime,status,serial,summaryDetail,userId) " +
//                        "VALUES(@id,@title,@content,@createTime,@lastModifyTime,@status,@serial,@summaryDetail,@userId)";
                String sql = "INSERT INTO note (id,title,content,createTime,lastModifyTime,status,serial,summaryDetail,userId) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)";
                addLog(new Log(Operate.ADD_NOTE, sql, note.getId().toString(), note.getUserId()), db);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void editNote(Note note) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", note.getTitle());
            values.put("content", note.getContent());
            values.put("createTime", note.getCreateTime().getTimeInMillis());
            values.put("lastModifyTime", note.getLastModifyTime().getTimeInMillis());
            values.put("status", note.getStatus());
            values.put("serial", note.getSerial());
            values.put("summaryDetail", note.isListAllContent());
            values.put("userId", note.getUserId().toString());
            db.update("note", values, "id=?", new String[]{note.getId().toString()});
//            String sql = "UPDATE note " +
//                    "SET title=@title,content=@content,createTime=@createTime,lastModifyTime=@lastModifyTime,status=@status,serial=@serial,summaryDetail=@summaryDetail,userId=@userId " +
//                    "WHERE id=@id";
            String sql = "UPDATE note " +
                    "SET title=?,content=?,createTime=?,lastModifyTime=?,status=?,serial=?,summaryDetail=?,userId=? " +
                    "WHERE id=?";
            addLog(new Log(Operate.UPDATE_NOTE, sql, note.getId().toString() + "," + note.getStatus(), note.getUserId()), db);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    private String stringValue(String value) {
        return value == null ? "null" : "'" + value + "'";
    }

    private String booleanValue(boolean value) {
        return value ? "1" : "0";
    }

    public void deleteNote(UUID noteId, UUID userId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("note", "id=?", new String[]{noteId.toString()});

//            String sql = "DELETE FROM note WHERE id=@id";
            String sql = "DELETE FROM note WHERE id=?";
            addLog(new Log(Operate.DELETE_NOTE, sql, noteId.toString(), userId), db);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void deleteNotes(int status, UUID userId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("note", "userId=? AND status=?", new String[]{userId.toString(), status + ""});

//            String sql = "DELETE FROM note WHERE userId=@userId AND status=@status";
            String sql = "DELETE FROM note WHERE userId=? AND status=?";
            addLog(new Log(Operate.DELETE_NOTE_BY_STATUS, sql, status + "", userId), db);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public Setting getSetting(String key, UUID userId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("setting", null, "userId=? AND key=?", new String[]{userId.toString(), key}, null, null, null);
            if (cursor.moveToNext()) {
                Setting setting = new Setting(userId);
                setting.setId(UUID.fromString(cursor.getString(0)));
                setting.setKey(key);
                setting.setValue(cursor.getString(2));
                return setting;
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
        return null;
    }

    public void editSetting(Setting setting) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("key", setting.getKey());
            values.put("value", setting.getValue());
            db.update("setting", values, "id=?", new String[]{setting.getId().toString()});
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void deleteSetting(String key, UUID userId) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void addSetting(Setting setting) throws Exception {
        SQLiteDatabase db = null;
        try {
            if (getSetting(setting.getKey(), setting.getUserId()) != null) {
                throw new Exception(StringHelper.concat("当前用户已存在<", setting.getKey(), ">的配置数据。"));
            }
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", setting.getId().toString());
            values.put("key", setting.getKey());
            values.put("value", setting.getValue());
            values.put("userId", setting.getUserId().toString());
            db.insert("setting", "key", values);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void addLog(Log log, SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();
            values.put("id", log.getId().toString());
            values.put("sql", log.getSql());
            values.put("dateTime", log.getDateTime().getTimeInMillis());
            values.put("userId", log.getUserId().toString());
            values.put("operate", log.getOperate().toInt());
            values.put("sqlValues", log.getSqlValues());
            db.insert("log", "id", values);

            // 向服务器修改提交修改
            new Thread() {
                @Override
                public void run() {
                    toServer();
                    updateLastLogId();
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public List<Log> getLogs(UUID userId) {
        List<Log> result = new ArrayList<Log>();
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("log", null, "userId=?", new String[]{userId.toString()}, null, null, "dateTime ASC");
            while (cursor.moveToNext()) {
                Log log = new Log(userId);
                log.setId(UUID.fromString(cursor.getString(0)));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(1));
                log.setDateTime(calendar);
                log.setSql(cursor.getString(2));
                log.setOperate(Operate.fromInt(cursor.getInt(4)));
                log.setSqlValues(cursor.getString(5));
                result.add(log);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
        return result;
    }

    public void deleteLog(UUID id) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("log", "id=?", new String[]{id.toString()});
            db.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void cleanLog(UUID userId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("log", "userId=?", new String[]{userId.toString()});
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    private String requestResultStr;
    private boolean synchronizePass = true;

    private void toServer() {
        if (!synchronizePass)
            return;

        requestResultStr = "";
        String url = null;
        Note note = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        DataContext context = new DataContext(this.context);
        List<Log> logs = context.getLogs(_Session.CurrentUserId);
        for (Log log : logs) {
            switch (log.getOperate()) {
                case ADD_NOTE:
                    url = "http://" + _Session.ServerIp + "/Home/AddNote";
                    params.clear();
                    params.add(new BasicNameValuePair("dateTime", log.getDateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("sql", log.getSql()));
                    params.add(new BasicNameValuePair("Id", log.getSqlValues()));
                    params.add(new BasicNameValuePair("UserId", log.getUserId().toString()));


                    note = context.getNote(UUID.fromString(log.getSqlValues()));
                    if (note == null) break;
                    params.add(new BasicNameValuePair("Content", note.getContent()));
                    params.add(new BasicNameValuePair("CreateTime", note.getCreateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("LastModifyTime", note.getLastModifyTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("SummaryDetail", note.isListAllContent() ? "1" : "0"));
                    params.add(new BasicNameValuePair("Serial", note.getSerial() + ""));
                    params.add(new BasicNameValuePair("Status", note.getStatus() + ""));
                    params.add(new BasicNameValuePair("Title", note.getTitle()));

                    break;
                case UPDATE_NOTE:
                    url = "http://" + _Session.ServerIp + "/Home/UpdateNote";
                    params.clear();
                    params.add(new BasicNameValuePair("dateTime", log.getDateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("sql", log.getSql()));
                    params.add(new BasicNameValuePair("UserId", log.getUserId().toString()));
                    String[] values = log.getSqlValues().split(",");
                    params.add(new BasicNameValuePair("Id", values[0]));
                    params.add(new BasicNameValuePair("Status", values[1]));

                    note = context.getNote(UUID.fromString(values[0]));
                    if (note == null) break;
                    params.add(new BasicNameValuePair("Title", note.getTitle()));
                    params.add(new BasicNameValuePair("Content", note.getContent()));
                    params.add(new BasicNameValuePair("CreateTime", note.getCreateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("LastModifyTime", note.getLastModifyTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("SummaryDetail", note.isListAllContent() ? "1" : "0"));
                    params.add(new BasicNameValuePair("Serial", note.getSerial() + ""));

                    break;
                case DELETE_NOTE:
                    url = "http://" + _Session.ServerIp + "/Home/DeleteNote";
                    params.clear();
                    params.add(new BasicNameValuePair("dateTime", log.getDateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("sql", log.getSql()));
                    params.add(new BasicNameValuePair("userId", log.getUserId().toString()));
                    params.add(new BasicNameValuePair("id", log.getSqlValues()));

                    break;
                case DELETE_NOTE_BY_STATUS:
                    url = "http://" + _Session.ServerIp + "/Home/DeleteNoteByStatus";
                    params.clear();
                    params.add(new BasicNameValuePair("dateTime", log.getDateTime().getTimeInMillis() + ""));
                    params.add(new BasicNameValuePair("sql", log.getSql()));
                    params.add(new BasicNameValuePair("userId", log.getUserId().toString()));
                    params.add(new BasicNameValuePair("status", log.getSqlValues()));

                    break;
                default:
                    break;
            }

            try {
                HttpPost httpRequest = new HttpPost(url);
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    requestResultStr = EntityUtils.toString(httpResponse.getEntity());
                    if (requestResultStr.equals("true")) {
                        context.deleteLog(log.getId());
                    } else {
                        synchronizePass = false;
                    }
                }
            } catch (ClientProtocolException e) {
                synchronizePass = false;
                requestResultStr = e.getMessage();
                break;
            } catch (IOException e) {
                synchronizePass = false;
                requestResultStr = "连接服务器失败！";
                break;
            } catch (Exception e) {
                synchronizePass = false;
                requestResultStr = e.getMessage();
                break;
            }
        }
    }

    private void updateLastLogId() {
        if (!synchronizePass)
            return;
        requestResultStr = "";
        try {
            DataContext context = new DataContext(this.context);
            Setting setting = context.getSetting("lastLogId", _Session.CurrentUserId);
            String uri = "http://" + _Session.ServerIp + "/Home/LastLogId?userId=" + _Session.CurrentUserId.toString() + "&id=";
            if (setting == null) {
                uri += "0";
                setting = new Setting(_Session.CurrentUserId);
                setting.setKey("lastLogId");
                context.addSetting(setting);
            } else {
                uri += setting.getValue();
            }
            HttpGet httpRequest = new HttpGet(uri);
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            int asdf = httpResponse.getStatusLine().getStatusCode();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                requestResultStr = EntityUtils.toString(httpResponse.getEntity());
                if (!requestResultStr.isEmpty()) {
                    try {
                        int lastId = Integer.parseInt(requestResultStr.trim());
                        setting.setValue(lastId + "");
                        context.editSetting(setting);
                    } catch (Exception e) {
                        synchronizePass = false;
                    }
                }
            }
        } catch (ClientProtocolException e) {
            synchronizePass = false;
            requestResultStr = e.getMessage();
        } catch (IOException e) {
            synchronizePass = false;
            requestResultStr = "连接服务器失败！";
        } catch (Exception e) {
            synchronizePass = false;
            requestResultStr = e.getMessage();
        }
    }


}
