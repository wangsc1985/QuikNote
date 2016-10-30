package com.wangsc.quicknote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote._enum.From;
import com.wangsc.quicknote.dialog.CreatePasswordDialog;
import com.wangsc.quicknote.helper.Callback;
import com.wangsc.quicknote.helper.DateTime;
import com.wangsc.quicknote.helper._Session;
import com.wangsc.quicknote.model.Category;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote.model.Note;
import com.wangsc.quicknote.model.Setting;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NoteDetailsActivity extends AppCompatActivity {

    private List<Note> notes;
    private UUID id;
    private Note note;
    private DataContext databaseContext;
    private List<Category> categories;
    private List<com.wangsc.quicknote.model.Tag> tags;
    private From from;
    private int sizeModel;
    private boolean changed = false;

    private TextView title, content, createTime, modifyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseContext = new DataContext(NoteDetailsActivity.this);

        Setting setting = databaseContext.getSetting("sizeModel", _Session.CurrentUserId);
        if (setting != null) {
            sizeModel = Integer.parseInt(setting.getValue());
        } else {
            sizeModel = _Session.default_size_model;
            Setting set = new Setting(_Session.CurrentUserId);
            set.setKey("sizeModel");
            set.setValue(sizeModel + "");
            try {
                databaseContext.addSetting(set);
            } catch (Exception e) {
                Toast.makeText(NoteDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        title = (TextView) findViewById(R.id.textView_title);
        this.setTextViewSize(title);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.text.ClipboardManager clipboarManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboarManager.setText(title.getText() + "\n\n" + content.getText());
                Toast.makeText(NoteDetailsActivity.this, "此文已复制", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        content = (TextView) findViewById(R.id.textView_content);
        this.setTextViewSize(content);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        createTime = (TextView) findViewById(R.id.textView_createTime);
        modifyTime = (TextView) findViewById(R.id.textView_modityTime);

        Intent intent = this.getIntent();
        from = From.valueOf(intent.getIntExtra("from", 0));
        id = UUID.fromString(intent.getStringExtra("id"));

        try {
            getNoteInfo(id);
        } catch (IOException e) {
            Toast.makeText(NoteDetailsActivity.this, "初始化出现IOExcetion", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(NoteDetailsActivity.this, "初始化出现ClassNotFoundException", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item=null;
        if (from == From.MainToDetails) {
            getMenuInflater().inflate(R.menu.menu_note_details, menu);
            item = menu.getItem(1);
        } else if (from == From.HiddenToDetails) {
            getMenuInflater().inflate(R.menu.menu_hidden_details, menu);
            item = menu.getItem(1);
        } else if (from == From.RecycleToDetails) {
            getMenuInflater().inflate(R.menu.menu_recycle_details, menu);
            item = menu.getItem(0);
        }
        if (note.isListAllContent()) {
            item.setTitle(listShowSummary);
        } else {
            item.setTitle(listShowAll);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuItemSelected(item);
    }

    private boolean menuItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_note_details_del) {
            note.setStatus(0);
            databaseContext.editNote(note);
            Intent intent = new Intent(NoteDetailsActivity.this, MainActivity.class);
            setResult(CHANGED, intent);
            NoteDetailsActivity.this.finish();
        } else if (id == R.id.menu_note_details_hidden) {
            Setting setting = databaseContext.getSetting("password", _Session.CurrentUserId);
            final String pw = setting == null ? null : setting.getValue();
            if (pw == null) {
                new CreatePasswordDialog(NoteDetailsActivity.this, new Callback() {
                    @Override
                    public void run() throws IOException, ClassNotFoundException {
                        note.setStatus(2);
                        databaseContext.editNote(note);
                        Intent intent = new Intent(NoteDetailsActivity.this, MainActivity.class);
                        setResult(CHANGED, intent);
                        NoteDetailsActivity.this.finish();
                    }
                }).show();
            } else {
                note.setStatus(2);
                databaseContext.editNote(note);
                Intent intent = new Intent(NoteDetailsActivity.this, MainActivity.class);
                setResult(CHANGED, intent);
                NoteDetailsActivity.this.finish();
            }
        } else if (id == R.id.menu_hidden_details_del) {
            note.setStatus(0);
            databaseContext.editNote(note);
            Intent intent = new Intent(NoteDetailsActivity.this, MainActivity.class);
            setResult(CHANGED, intent);
            NoteDetailsActivity.this.finish();
        } else if (id == R.id.menu_hidden_details_out) {
            note.setStatus(1);
            databaseContext.editNote(note);
            Intent intent = new Intent(NoteDetailsActivity.this, HiddenActivity.class);
            setResult(CHANGED, intent);
            NoteDetailsActivity.this.finish();
        } else if (id == R.id.menu_recycle_details_del) {
            databaseContext.deleteNote(note.getId(), _Session.CurrentUserId);
            Intent intent = new Intent(NoteDetailsActivity.this, RecycleActivity.class);
            setResult(CHANGED, intent);
            NoteDetailsActivity.this.finish();
        } else if (id == R.id.menu_recycle_details_back) {
            note.setStatus(1);
            databaseContext.editNote(note);
            Intent intent = new Intent(NoteDetailsActivity.this, RecycleActivity.class);
            setResult(CHANGED, intent);
            NoteDetailsActivity.this.finish();
        } else if (id == R.id.menu_note_details_summary) {
            if (item.getTitle().toString().equals(listShowAll)) {
                note.setListAllContent(true);
                item.setTitle(listShowSummary);
            } else {
                note.setListAllContent(false);
                item.setTitle(listShowAll);
            }
            changed = true;
        } else if (id == R.id.menu_hidden_details_summary) {
            if (item.getTitle().toString().equals(listShowAll)) {
                note.setListAllContent(true);
                item.setTitle(listShowSummary);
            } else {
                note.setListAllContent(false);
                item.setTitle(listShowAll);
            }
            changed = true;
        } else if (id == R.id.menu_recycle_details_summary) {
            if (item.getTitle().toString().equals(listShowAll)) {
                note.setListAllContent(true);
                item.setTitle(listShowSummary);
            } else {
                note.setListAllContent(false);
                item.setTitle(listShowAll);
            }
            changed = true;
        }
        return true;
    }

    private static final String listShowAll = "主页全文显示";
    private static final String listShowSummary = "主页摘要显示";

    private void getNoteInfo(UUID id) throws IOException, ClassNotFoundException {
        note = databaseContext.getNote(id);

        if (note != null) {
            title.setText(note.getTitle());
            content.setText(note.getContent());

            createTime.setText( DateTime.fromCalendar(note.getCreateTime()).toLongDateString());
            modifyTime.setText( DateTime.fromCalendar(note.getLastModifyTime()).toLongDateString());
        }
    }

    public static final int CHANGED = 1;
    public static final int NO_CHANGED = 0;

    @Override
    public void onBackPressed() {
        if (changed) {
            databaseContext.editNote(note);
            setResult(CHANGED);
        } else {
            setResult(NO_CHANGED);
        }
        super.onBackPressed();
    }

    private void setTextViewSize(TextView et) {
        if (et.getInputType() != InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            switch (sizeModel) {
                case 1:
                    et.setTextSize(_Session.details_title_size_smaller);
                    break;
                case 2:
                    et.setTextSize(_Session.details_title_size_small);
                    break;
                case 3:
                    et.setTextSize(_Session.details_title_size_normal);
                    break;
                case 4:
                    et.setTextSize(_Session.details_title_size_big);
                    break;
                case 5:
                    et.setTextSize(_Session.details_title_size_biger);
                    break;
                default:
                    et.setTextSize(_Session.details_title_size_normal);
                    break;
            }
        else
            switch (sizeModel) {
                case 1:
                    et.setTextSize(_Session.details_content_size_smaller);
                    break;
                case 2:
                    et.setTextSize(_Session.details_content_size_small);
                    break;
                case 3:
                    et.setTextSize(_Session.details_content_size_normal);
                    break;
                case 4:
                    et.setTextSize(_Session.details_content_size_big);
                    break;
                case 5:
                    et.setTextSize(_Session.details_content_size_biger);
                    break;
                default:
                    et.setTextSize(_Session.details_content_size_normal);
                    break;
            }
    }
}
