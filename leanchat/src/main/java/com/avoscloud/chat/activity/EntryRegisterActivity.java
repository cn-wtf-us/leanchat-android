package com.avoscloud.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.App;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.leanchatlib.activity.AVBaseActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.redpacket.RedPacketUtils;
import com.avoscloud.leanchatlib.redpacket.RequestTask;

public class EntryRegisterActivity extends AVBaseActivity {
    View registerButton;
    EditText usernameEdit, passwordEdit, emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_register_activity);
        findView();
        setTitle(App.ctx.getString(R.string.register));
        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                register();
            }
        });
    }

    private void findView() {
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        emailEdit = (EditText) findViewById(R.id.ensurePasswordEdit);
        registerButton = findViewById(R.id.btn_register);
    }

    private void register() {
        final String name = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        String againPassword = emailEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Utils.toast(R.string.username_cannot_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Utils.toast(R.string.password_can_not_null);
            return;
        }
        if (!againPassword.equals(password)) {
            Utils.toast(R.string.password_not_consistent);
            return;
        }

        LeanchatUser.signUpByNameAndPwd(name, password, new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Utils.toast(App.ctx.getString(R.string.registerFailed) + e.getMessage());
                } else {
                    Utils.toast(R.string.registerSucceed);
                    imLogin();
                    initUserData();
                }
            }
        });
    }

    private void imLogin() {
        ChatManager.getInstance().openClient(this, LeanchatUser.getCurrentUserId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (filterException(e)) {
                    RequestTask.getInstance().initRedPacketNet(EntryRegisterActivity.this, LeanchatUser.getCurrentUserId());
                    Intent intent = new Intent(EntryRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 初始化登录用户数据
     */
    private void initUserData() {
        LeanchatUser curuser = LeanchatUser.getCurrentUser();
        if (!TextUtils.isEmpty(LeanchatUser.getCurrentUserId())) {
            RedPacketUtils.getInstance().setUserid(curuser.getCurrentUserId());
        } else {
            RedPacketUtils.getInstance().setUserid("");
        }
        if (curuser != null) {
            if (!TextUtils.isEmpty(curuser.getUsername())) {
                RedPacketUtils.getInstance().setUserName(curuser.getUsername());
            } else {
                RedPacketUtils.getInstance().setUserName("");
            }
            if (!TextUtils.isEmpty(curuser.getAvatarUrl())) {
                RedPacketUtils.getInstance().setUserAvatar(curuser.getAvatarUrl());
            } else {
                RedPacketUtils.getInstance().setUserAvatar("none");
            }
        }
    }
}
