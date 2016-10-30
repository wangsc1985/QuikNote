package com.wangsc.quicknote.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wangsc.quicknote.R;
import com.wangsc.quicknote.helper.Callback;
import com.wangsc.quicknote.helper.MD5;

import java.io.IOException;

/**
 * Created by 阿弥陀佛 on 2015/11/17.
 */
public class ConfirmPasswordDialog {

    private Context context;
    private Dialog dialog;
    private EditText password;
    private Button btnClose, btnDel;
    private Callback callback;

    public ConfirmPasswordDialog(Context context, final String pwMD5, Callback callback) {
        this.context = context;
        this.callback = callback;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm_password);

        dialog.setTitle("请输入密码");

        password = (EditText) dialog.findViewById(R.id.editText_password);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password.getText().length()==4){
                    if (MD5.convert(password.getText().toString()).equals(pwMD5)) {
                        try {
                            ConfirmPasswordDialog.this.callback.run();
                            dialog.cancel();
                        } catch (ClassNotFoundException e) {
                            Toast.makeText(ConfirmPasswordDialog.this.context, "保存密码出现ClassNotFoundException", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(ConfirmPasswordDialog.this.context, "保存密码出现ClassNotFoundException", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ConfirmPasswordDialog.this.context, "密码不正确！", Toast.LENGTH_LONG).show();
                        password.setText("");
                    }
                }
            }
        });

        btnClose = (Button) dialog.findViewById(R.id.button_Close);
        btnDel = (Button) dialog.findViewById(R.id.button_del);

        btnClose.setOnClickListener(new View.OnClickListener() {
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
