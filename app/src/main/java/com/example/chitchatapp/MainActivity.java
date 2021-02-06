package com.example.chitchatapp;

import android.app.Application;

import com.example.chitchatapp.utils.Constant;
import com.qiscus.sdk.chat.core.QiscusCore;

public class MainActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        QiscusCore.setup(this, Constant.APP_ID_QISCUS);
    }
}