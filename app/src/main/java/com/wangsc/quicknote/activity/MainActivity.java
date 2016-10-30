package com.wangsc.quicknote.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote._enum.From;
import com.wangsc.quicknote._enum.Operate;
import com.wangsc.quicknote.dialog.ConfirmPasswordDialog;
import com.wangsc.quicknote.dialog.CreatePasswordDialog;
import com.wangsc.quicknote.helper.Callback;
import com.wangsc.quicknote.helper._Session;
import com.wangsc.quicknote.model.Category;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote.model.DatabaseHelper;
import com.wangsc.quicknote.model.Log;
import com.wangsc.quicknote.model.Note;
import com.wangsc.quicknote.model.Setting;
import com.wangsc.quicknote.model.Tag;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editTextContent;
    private Button btnMore, btnSave, btnSynchronize;
    private ImageView ivMore;
    private LinearLayout layoutNoteList;
    private TableLayout perchLayout;
    private TableLayout quickAddLayout;
    private FloatingActionButton fab;

    private List<Note> notes;
    private List<Category> categories;
    private List<Tag> tags;
    private int sizeModel;
    private NetCheckReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        receiver = new NetCheckReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(receiver, filter);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        InitializeFields();
        InitializeEvents();
        ListNotes(1);

//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//        if (info != null && info.isAvailable()) {
//            Sync(true);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    public class NetCheckReceiver extends BroadcastReceiver {

        private ConnectivityManager connectivityManager;
        private NetworkInfo info;


        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    android.util.Log.i("info", "网络状态已经改变");
                    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    info = connectivityManager.getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        MainActivity.this.Sync(true);
                    }
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
            }
        }
    }


    // 字体
//    private Typeface fontHWZS;

    /**
     * 变量初始化
     */
    private void InitializeFields() {

        editTextContent = (EditText) findViewById(R.id.editText_content);
        btnMore = (Button) findViewById(R.id.button_more);
        btnSave = (Button) findViewById(R.id.button_save);
        ivMore = (ImageView) findViewById(R.id.imageView_more);
        layoutNoteList = (LinearLayout) findViewById(R.id.layout_noteList);
        perchLayout = (TableLayout) layoutNoteList.getChildAt(0);
        quickAddLayout = (TableLayout) findViewById(R.id.quick_add);
        btnSynchronize = (Button) findViewById(R.id.button_Synchronize);
    }

    public void Sync(final boolean isAuto) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            Toast.makeText(MainActivity.this, "无法连接网络!", Toast.LENGTH_SHORT);
            return;
        }
        synchronizePass = true;
        btnSynchronize.setText("同步中....");
        btnSynchronize.setEnabled(false);
        btnSynchronize.setTextColor(Color.BLACK);
        new Thread() {
            @Override
            public void run() {
                Setting setting = new DataContext(MainActivity.this).getSetting("lastLogId", _Session.CurrentUserId);
                if (setting == null) {
                    getAll(_Session.CurrentUserId.toString());
                } else {
                    fromServer();
                    toServer();
                }
                updateLastLogId();
                if (isAuto) {
                    if (synchronizePass)
                        sendHanderMessage(AUTO_SYNCHRONIZE_SUCCESS);
                    else
                        sendHanderMessage(AUTO_SYNCHRONIZE_FAIL);
                } else {
                    if (synchronizePass)
                        sendHanderMessage(SYNCHRONIZE_SUCCESS);
                    else
                        sendHanderMessage(SYNCHRONIZE_FAIL);
                }
            }
        }.start();
    }

    /**
     * 事件初始化
     */
    private void InitializeEvents() {


        btnSynchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sync(false);
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                intent.putExtra("id", "");
                intent.putExtra("from", From.MainToAdd.value());
                intent.putExtra("content", editTextContent.getText().toString());
                startActivityForResult(intent, TO_ADD);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        editTextContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String dsf = editTextContent.getText().toString();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (editTextContent.getText().toString().isEmpty())
                        return true;
                    Note note = new Note(null, editTextContent.getText().toString(), _Session.CurrentUserId);
                    notes.add(note);
                    new DataContext(MainActivity.this).addNote(note);

                    final String id = note.getId().toString();
                    // 框架
                    LinearLayout linear = new LinearLayout(MainActivity.this);
                    linear.setBackgroundResource(R.drawable.abc_popup_background_mtrl_mult);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linear.setLayoutParams(layoutParams);
                    linear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("from", From.MainToDetails.value());
                            startActivityForResult(intent, TO_DETAILS);
                        }
                    });
                    linear.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("from", From.MainToEdit.value());
                            startActivityForResult(intent, TO_EDIT);
                            return true;
                        }
                    });

                    // 标题
                    if (note.getTitle() != null) {
                        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams0.setMargins(20, 20, 20, 10);
                        TextView tvTitle = new TextView(MainActivity.this);
                        MainActivity.this.setTextViewSize(tvTitle);
                        tvTitle.setTextColor(Color.BLACK);
                        tvTitle.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                        tvTitle.getPaint().setFakeBoldText(true);
                        tvTitle.setLayoutParams(layoutParams0);
                        tvTitle.setText(note.getTitle());
                        linear.addView(tvTitle);
                    }

                    // 内容
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (note.getTitle() == null)
                        layoutParams1.setMargins(20, 20, 20, 20);
                    else
                        layoutParams1.setMargins(20, 0, 20, 20);
                    TextView tvContent = new TextView(MainActivity.this);
                    MainActivity.this.setTextViewSize(tvContent);
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
                    editTextContent.setText("");
                    return true;
                } else {
                    return false;
                }
            }
        });
        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextContent.getText().toString().isEmpty()) {
                    ivMore.setVisibility(View.INVISIBLE);
                } else {
                    ivMore.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Calendar lastBackPressed = null;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (lastBackPressed != null && (Calendar.getInstance().getTimeInMillis() - lastBackPressed.getTimeInMillis()) <= 3000) {
                super.onBackPressed();
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                lastBackPressed = Calendar.getInstance();
            }
        }
    }

    private void ListNotes(int noteStatus) {
        DataContext context = new DataContext(MainActivity.this);
        notes = context.getNotes(noteStatus, _Session.CurrentUserId);

        Setting setting = context.getSetting("sizeModel", _Session.CurrentUserId);
        if (setting != null) {
            sizeModel = Integer.parseInt(setting.getValue());
        } else {
            sizeModel = _Session.default_size_model;
//            Setting set = new Setting(_Session.CurrentUserId);
//            set.setKey("sizeModel");
//            set.setValue(sizeModel + "");
//            try {
//                context.addSetting(set);
//            } catch (Exception e) {
//                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            }
        }

        for (Note note : notes) {

//            new DataContext(MainActivity.this).editNote(note);

            final String id = note.getId().toString();
            // 框架
            LinearLayout linear = new LinearLayout(MainActivity.this);
            linear.setBackgroundResource(R.drawable.abc_popup_background_mtrl_mult);
            linear.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linear.setLayoutParams(layoutParams);
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("from", From.MainToDetails.value());
                    startActivityForResult(intent, TO_DETAILS);
                }
            });
            linear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("from", From.MainToEdit.value());
                    startActivityForResult(intent, TO_EDIT);
                    return true;
                }
            });

            // 标题
            if (!(note.getTitle() == null || (note.getTitle() != null && note.getTitle().isEmpty()))) {
                LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams0.setMargins(20, 20, 20, 10);
                TextView tvTitle = new TextView(MainActivity.this);
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
            TextView tvContent = new TextView(MainActivity.this);
            this.setTextViewSize(tvContent);
            tvContent.setLineSpacing(1, 1.2f);
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

    public final static int TO_EDIT = 1;
    public final static int TO_ADD = 2;
    public final static int TO_DETAILS = 3;
    public final static int TO_RECYCLE = 4;
    public final static int TO_HIDDEN = 5;

    // requestCode == 1 编辑操作
    // requestCode == 2 添加操作
    // resultCode == 1 由NoteEditActivity返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_EDIT) { // 进入编辑
            if (resultCode == NoteEditActivity.CHANGED) {
                reloadNoteList();
            }
        } else if (requestCode == TO_ADD) { // 进入添加
            if (resultCode == NoteEditActivity.CHANGED) {
                reloadNoteList();
                editTextContent.setText("");
            }
        } else if (requestCode == TO_DETAILS) { // 进入详细
            if (resultCode == NoteDetailsActivity.CHANGED) {
                reloadNoteList();
            }
        } else if (requestCode == TO_RECYCLE) { // 进入回收站
            if (resultCode == RecycleActivity.CHANGED) {
                reloadNoteList();
            }
        } else if (requestCode == TO_HIDDEN) { // 进入私密空间
            if (resultCode == HiddenActivity.CHANGED) {
                reloadNoteList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadNoteList() {
        layoutNoteList.removeAllViews();
        layoutNoteList.addView(perchLayout);
        ListNotes(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return menuItemSelected(id);
    }

    private boolean menuItemSelected(int id) {
        DataContext context = new DataContext(MainActivity.this);
        if (id == R.id.nav_recycle) {
            startActivityForResult(new Intent(MainActivity.this, RecycleActivity.class), 4);
        } else if (id == R.id.nav_secret) {
            Setting setting = context.getSetting("password", _Session.CurrentUserId);
            final String pw = setting == null ? null : setting.getValue();
            if (pw == null) {
                new CreatePasswordDialog(MainActivity.this, new Callback() {
                    @Override
                    public void run() {
                        startActivityForResult(new Intent(MainActivity.this, HiddenActivity.class), 5);
                    }
                }).show();
            } else {
                new ConfirmPasswordDialog(MainActivity.this, pw, new Callback() {
                    @Override
                    public void run() throws IOException, ClassNotFoundException {
                        startActivityForResult(new Intent(MainActivity.this, HiddenActivity.class), 5);
                    }
                }).show();
            }

        } else if (id == R.id.nav_smaller) {
            editSizeModelSetting(context, "1");
            reloadNoteList();
        } else if (id == R.id.nav_small) {
            editSizeModelSetting(context, "2");
            reloadNoteList();
        } else if (id == R.id.nav_normal) {
            editSizeModelSetting(context, "3");
            reloadNoteList();
        } else if (id == R.id.nav_big) {
            editSizeModelSetting(context, "4");
            reloadNoteList();
        } else if (id == R.id.nav_biger) {
            editSizeModelSetting(context, "5");
            reloadNoteList();
        }
//        else if (id == R.id.nav_test) {
//            context.cleanLog(_Session.CurrentUserId);
//            Setting setting = context.getSetting("lastLogId", _Session.CurrentUserId);
//            if (setting != null) {
//                setting.setValue("0");
//                context.editSetting(setting);
//            }
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void editSizeModelSetting(DataContext context, String value) {
        Setting setting = context.getSetting("sizeModel", _Session.CurrentUserId);
        if (setting != null) {
            setting.setValue(value);
            context.editSetting(setting);
        } else {
            Setting set = new Setting(_Session.CurrentUserId);
            set.setKey("sizeModel");
            set.setValue(value);
            try {
                context.addSetting(set);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        return menuItemSelected(id);
    }

    private Message handlerMSG = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                switch (msg.what) {
                    case SYNCHRONIZE_FAIL:
                        new AlertDialog.Builder(MainActivity.this).setMessage(requestResultStr).setTitle("错误提示").setCancelable(false).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
                        btnSynchronize.setText("同步");
                        btnSynchronize.setEnabled(true);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case SYNCHRONIZE_SUCCESS:
                        btnSynchronize.setText("同步");
                        btnSynchronize.setEnabled(true);
                        reloadNoteList();
                        Toast.makeText(MainActivity.this, "数据已同步", Toast.LENGTH_LONG).show();
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case AUTO_SYNCHRONIZE_SUCCESS:
                        btnSynchronize.setText("同步");
                        btnSynchronize.setEnabled(true);
                        reloadNoteList();
                        Toast.makeText(MainActivity.this, "数据已同步", Toast.LENGTH_LONG).show();
                        break;
                    case AUTO_SYNCHRONIZE_FAIL:
                        btnSynchronize.setText("同步");
                        btnSynchronize.setEnabled(true);
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

        ;

        private void sendHanderMessage(int msg) {
            handlerMSG = handler.obtainMessage(msg);
            handler.sendMessage(handlerMSG);
        }

        private String requestResultStr;
        private boolean synchronizePass = true;
        private final int SYNCHRONIZE_FAIL = 1;
        private final int SYNCHRONIZE_SUCCESS = 2;
        private final int GET_ALL_SUCCESS = 3;
        private final int GET_ALL_FAIL = 4;
        private final int AUTO_SYNCHRONIZE_FAIL = 6;
        private final int AUTO_SYNCHRONIZE_SUCCESS = 7;

        private void toServer() {
            if (!synchronizePass)
                return;

            requestResultStr = "";
            String url = null;
            Note note = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            DataContext context = new DataContext(MainActivity.this);
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
                        JSONObject json = new JSONObject();
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
                DataContext context = new DataContext(MainActivity.this);
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

        private void fromServer() {
            requestResultStr = "";
            try {
                DataContext context = new DataContext(MainActivity.this);
                Setting setting = context.getSetting("lastLogId", _Session.CurrentUserId);
                String logId = "0";
                if (setting == null) {
                    setting = new Setting(_Session.CurrentUserId);
                    setting.setKey("lastLogId");
                    context.addSetting(setting);
                } else {
                    logId = setting.getValue();
                }

                while (true) {
                    String uri = "http://" + _Session.ServerIp + "/Home/ToClient?userId=" + _Session.CurrentUserId.toString() + "&id=" + logId;
                    HttpGet httpRequest = new HttpGet(uri);
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        requestResultStr = EntityUtils.toString(httpResponse.getEntity());
                        try {
                            JSONObject obj = new JSONObject(requestResultStr);
                            // 服务器返回的json内容为空，说明没有更新的更改记录。
                            if (obj.length() == 0)
                                break;


                            logId = obj.getInt("logId") + "";

                            SQLiteDatabase db = new DatabaseHelper(MainActivity.this).getWritableDatabase();
                            ContentValues values = null;
                            try {
                                switch (Operate.fromInt(obj.getInt("operate"))) {
                                    case ADD_NOTE:
                                        JSONObject note = obj.getJSONObject("note");
                                        values = new ContentValues();
                                        values.put("id", note.getString("Id"));
                                        values.put("title", note.getString("Title"));
                                        values.put("content", note.getString("Content"));
                                        values.put("createTime", note.getLong("CreateTime"));
                                        values.put("lastModifyTime", note.getLong("LastModifyTime"));
                                        values.put("status", note.getInt("Status"));
                                        values.put("serial", note.getInt("Serial"));
                                        values.put("summaryDetail", note.getInt("SummaryDetail"));
                                        values.put("userId", note.getString("UserId"));
                                        db.insert("note", "id", values);
                                        break;
                                    case UPDATE_NOTE:
                                        JSONObject note1 = obj.getJSONObject("note");
                                        values = new ContentValues();
                                        values.put("title", note1.getString("Title"));
                                        values.put("content", note1.getString("Content"));
                                        values.put("createTime", note1.getLong("CreateTime"));
                                        values.put("lastModifyTime", note1.getLong("LastModifyTime"));
                                        values.put("status", note1.getInt("Status"));
                                        values.put("serial", note1.getInt("Serial"));
                                        values.put("summaryDetail", note1.getInt("SummaryDetail"));
                                        values.put("userId", note1.getString("UserId"));
                                        db.update("note", values, "id=?", new String[]{note1.getString("Id")});
                                        break;
                                    case DELETE_NOTE:
                                        db.delete("note", "id=?", new String[]{obj.getString("Id")});
                                        break;
                                    case DELETE_NOTE_BY_STATUS:
                                        db.delete("note", "userId=? AND status=?", new String[]{obj.getString("userId"), obj.getString("status")});
                                        break;
                                    default:
                                        break;
                                }

                                setting.setValue(logId);
                                context.editSetting(setting);
                            } catch (Exception e) {
                                synchronizePass = false;
                                throw e;
                            } finally {
                                db.close();
                            }
                        } catch (Exception ex) {
                            // 如果服务器返回不是json数据，说明服务器端执行触发异常
                            synchronizePass = false;
                            break;
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

        private void getAll(String userId) {
            String uri = "http://" + _Session.ServerIp + "/Home/GetAll?userId=" + userId;
            HttpGet httpRequest = new HttpGet(uri);
            try {
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    requestResultStr = EntityUtils.toString(httpResponse.getEntity());

                    JSONArray noteArray = new JSONArray(requestResultStr);
                    SQLiteDatabase db = new DatabaseHelper(MainActivity.this).getWritableDatabase();
                    DataContext dataContext = new DataContext(MainActivity.this);

                    try {
                        db.beginTransaction();
                        for (int i = 0; i < noteArray.length(); i++) {
                            JSONObject obj = noteArray.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            String abd = null;
                            String sdf = obj.getString("title");
                            values.put("id", obj.getString("id"));
                            values.put("title", obj.getString("title"));
                            values.put("content", obj.getString("content"));
                            values.put("createTime", obj.getLong("createTime"));
                            values.put("lastModifyTime", obj.getLong("lastModifyTime"));
                            values.put("status", obj.getInt("status"));
                            values.put("serial", obj.getInt("serial"));
                            values.put("summaryDetail", obj.getInt("summaryDetail"));
                            values.put("userId", obj.getString("userId"));
                            db.insert("note", "id", values);
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        db.endTransaction();
                        db.close();
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
