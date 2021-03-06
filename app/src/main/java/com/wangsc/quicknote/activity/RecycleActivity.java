package com.wangsc.quicknote.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote.model.Category;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote._enum.From;
import com.wangsc.quicknote.model.Note;
import com.wangsc.quicknote.model.Setting;
import com.wangsc.quicknote.model.Tag;
import com.wangsc.quicknote.helper._Session;

import java.util.List;

public class RecycleActivity extends AppCompatActivity {

    private LinearLayout layoutNoteList;
    private TableLayout perchLayout;

    private List<Note> notes;
    private List<Category> categories;
    private List<Tag> tags;

    private boolean changed = false;
    private int sizeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RecycleActivity.this);
                dialog.setTitle("删除确认");
                dialog.setMessage("确认要清空回收站吗？");
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DataContext(RecycleActivity.this).deleteNotes(0, _Session.CurrentUserId);
                        RecycleActivity.this.reloadNoteList();
                        dialog.cancel();
                        Toast.makeText(RecycleActivity.this, "回收站已清空", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
            }
        });

        InitializeFields();
        InitializeEvents();
        ListNotes(0);
    }

    /**
     * 变量初始化
     */
    private void InitializeFields() {

        layoutNoteList = (LinearLayout) findViewById(R.id.layout_noteList);
        perchLayout = (TableLayout) layoutNoteList.getChildAt(0);

        // 字体
//        AssetManager mgr = getAssets();//得到AssetManager
//        fontHWZS = Typeface.createFromAsset(mgr, "fonts/STZHONGS.TTF");
    }

    /**
     * 事件初始化
     */
    private void InitializeEvents() {

    }

    private void ListNotes(int noteStatus) {

        DataContext context = new DataContext(RecycleActivity.this);
        notes = context.getNotes(noteStatus, _Session.CurrentUserId);

        Setting setting = context.getSetting("sizeModel", _Session.CurrentUserId);
        if (setting != null) {
            sizeModel = Integer.parseInt(setting.getValue());
        } else {
            sizeModel = _Session.default_size_model;
            Setting set = new Setting(_Session.CurrentUserId);
            set.setKey("sizeModel");
            set.setValue(sizeModel + "");
            try {
                context.addSetting(set);
            } catch (Exception e) {
                Toast.makeText(RecycleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        for (Note note : notes) {

            final String id = note.getId().toString();
            // 框架
            LinearLayout linear = new LinearLayout(RecycleActivity.this);
            linear.setBackgroundResource(R.drawable.abc_popup_background_mtrl_mult);
            linear.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linear.setLayoutParams(layoutParams);
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecycleActivity.this, NoteDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("from", From.RecycleToDetails.value());
                    startActivityForResult(intent, TO_DETAILS);
                }
            });

            // 标题
            if (!(note.getTitle() == null || (note.getTitle() != null && note.getTitle().isEmpty()))) {
                LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams0.setMargins(20, 20, 20, 10);
                TextView tvTitle = new TextView(RecycleActivity.this);
                this.setTextViewSize(tvTitle);
                tvTitle.setTextColor(Color.BLACK);
                tvTitle.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                tvTitle.getPaint().setFakeBoldText(true);
                tvTitle.setLayoutParams(layoutParams0);
                tvTitle.setText(note.getTitle());
                linear.addView(tvTitle);
            }

            // 内容
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (note.getTitle() == null || (note.getTitle() != null && note.getTitle().isEmpty()))
                layoutParams1.setMargins(20, 20, 20, 20);
            else
                layoutParams1.setMargins(20, 0, 20, 20);
            TextView tvContent = new TextView(RecycleActivity.this);
            this.setTextViewSize(tvContent);
            tvContent.setTextColor(Color.BLACK);
            tvContent.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            tvContent.setLayoutParams(layoutParams1);
            String content = note.getContent();
            tvContent.setText(content);
            if (!note.isListAllContent()) {
                tvContent.setMaxLines(4);
                tvContent.setEllipsize(TextUtils.TruncateAt.END);
            }
            linear.addView(tvContent);

            layoutNoteList.addView(linear, 0);
        }

    }

    private void setTextViewSize(TextView tv) {
        switch (sizeModel) {
            case 1:
                tv.setTextSize(_Session.home_text_size_smaller);
                break;
            case 2:
                tv.setTextSize(_Session.home_text_size_small);
                break;
            case 3:
                tv.setTextSize(_Session.home_text_size_normal);
                break;
            case 4:
                tv.setTextSize(_Session.home_text_size_big);
                break;
            case 5:
                tv.setTextSize(_Session.home_text_size_biger);
                break;
            default:
                tv.setTextSize(_Session.home_text_size_normal);
                break;
        }
    }

    // requestCode == 1 编辑操作
    // requestCode == 2 添加操作
    // resultCode == 1 由NoteEditActivity返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_DETAILS) { // 进入详细
            if (resultCode == NoteDetailsActivity.CHANGED) {
                reloadNoteList();
                changed = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadNoteList() {
        layoutNoteList.removeAllViews();
        ListNotes(0);
    }

    public static final int TO_DETAILS = 3;
    public static final int NO_CHANGED = 0;
    public static final int CHANGED = 1;
    @Override
    public void onBackPressed() {
        if (changed) {
            setResult(CHANGED);
        } else {
            setResult(NO_CHANGED);
        }
        super.onBackPressed();
    }
}
