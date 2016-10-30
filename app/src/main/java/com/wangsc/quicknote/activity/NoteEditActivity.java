package com.wangsc.quicknote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote._enum.From;
import com.wangsc.quicknote.helper._Session;
import com.wangsc.quicknote.model.Category;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote.model.Note;
import com.wangsc.quicknote.model.Setting;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class NoteEditActivity extends AppCompatActivity {


    private List<Note> notes;
    private List<Category> categories;
    private List<com.wangsc.quicknote.model.Tag> tags;
    private DataContext databaseContext;
    private Note note;
    private boolean changed = false;
    private From from;
    private int sizeModel;

    private EditText title, content;

    private void InitializeFields() {
        title = (EditText) findViewById(R.id.editText_title);
        content = (EditText) findViewById(R.id.editText_content);
        this.setEditTextSize(title);
        this.setEditTextSize(content);
    }

    private void InitializeEvents() {
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changed = true;
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changed = true;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseContext = new DataContext(NoteEditActivity.this);
        Intent intent = this.getIntent();
        from = From.valueOf(intent.getIntExtra("from", 0));
        String strID = intent.getStringExtra("id");
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
                Toast.makeText(NoteEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        InitializeFields();

        if (from == From.MainToEdit || from == From.HiddenToEdit) { // 编辑界面
            this.setTitle("编辑笔记");
            UUID id = UUID.fromString(strID);

            note = databaseContext.getNote(id);
            if (note != null) {
                title.setText(note.getTitle());
                content.setText(note.getContent());
            }

        } else if (from == From.MainToAdd) { // 添加界面
            this.setTitle("添加笔记");
            note = new Note(_Session.CurrentUserId);
            content.setText(this.getIntent().getStringExtra("content"));
        }

        InitializeEvents();
    }

    public static final int CHANGED = 1;
    public static final int NO_CHANGED = 0;

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if (from == From.MainToAdd || from == From.MainToEdit) {
            intent = new Intent(NoteEditActivity.this, MainActivity.class);
        }
        if (from == From.HiddenToEdit) {
            intent = new Intent(NoteEditActivity.this, HiddenActivity.class);
        }
        if (changed) {
            note.setTitle(title.getText().toString());
            note.setContent(content.getText().toString());
            note.setLastModifyTime(Calendar.getInstance());
            if (from == From.MainToAdd) {
                if (!note.getTitle().isEmpty() || !note.getContent().isEmpty()) {
                    databaseContext.addNote(note);
                    setResult(CHANGED);
                }
            } else {
                if (!note.getTitle().isEmpty() || !note.getContent().isEmpty()) {
                    databaseContext.editNote(note);
                    setResult(CHANGED);
                }
            }
        } else {
            setResult(NO_CHANGED);
        }
        super.onBackPressed();
    }

    private void setEditTextSize(EditText et) {
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
