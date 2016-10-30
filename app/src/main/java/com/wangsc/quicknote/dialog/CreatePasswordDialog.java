package com.wangsc.quicknote.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote.helper.Callback;
import com.wangsc.quicknote.helper.MD5;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote.model.Setting;
import com.wangsc.quicknote.helper._Session;

import java.io.IOException;

/**
 * Created by 阿弥陀佛 on 2015/11/17.
 */
public class CreatePasswordDialog {

    protected class NumberGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    private Context context;
    private Dialog dialog;
    private EditText password1, password2;
    private Button btnOK, btnDel;
    private Callback callback;

    public CreatePasswordDialog(final Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_password);
        dialog.setTitle("请先为私密空间创建4位密码");

        password1 = (EditText) dialog.findViewById(R.id.editText_password1);
        password2 = (EditText) dialog.findViewById(R.id.editText_password2);
        btnOK= (Button) dialog.findViewById(R.id.button_OK);
        btnDel = (Button) dialog.findViewById(R.id.button_del);
        password1.requestFocus();
        password1.requestFocusFromTouch();

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password1.getText().toString().length() == 4) {
                    password2.requestFocus();
                    password2.requestFocusFromTouch();
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password2.getText().toString().length() == 4) {
                    if (password1.getText().toString().isEmpty()) {
                        Toast.makeText(CreatePasswordDialog.this.context, "密码不能为空！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password1.getText().toString().equals(password2.getText().toString())) {
                        try {
                            Setting setting = new Setting(_Session.CurrentUserId);
                            setting.setKey("password");
                            setting.setValue(MD5.convert(password1.getText().toString()));
                            new DataContext(context).addSetting(setting);
                            CreatePasswordDialog.this.callback.run();
                            dialog.cancel();
                        } catch (ClassNotFoundException e) {
                            Toast.makeText(CreatePasswordDialog.this.context, "保存密码出现ClassNotFoundException", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(CreatePasswordDialog.this.context, "保存密码出现ClassNotFoundException", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(CreatePasswordDialog.this.context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreatePasswordDialog.this.context, "两次输入不一致！", Toast.LENGTH_LONG).show();
                        password2.setText("");
                    }
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public void show() {
        dialog.show();
    }
}
