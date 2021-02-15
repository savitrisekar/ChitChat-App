package com.example.chitchat;

import android.app.Application;
import android.os.Build;

import androidx.multidex.MultiDexApplication;

import com.example.chitchat.utils.Constant;
import com.qiscus.jupuk.Jupuk;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

public class ChitChatApp extends MultiDexApplication {
    private static ChitChatApp instance;

    private AppComponent component;

    public static ChitChatApp getInstance() {
        return instance;
    }

    private static void initEmoji() {
        EmojiManager.install(new EmojiOneProvider());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = new AppComponent(this);

        Nirmana.init(this);
        QiscusCore.setup(this, Constant.APP_ID_QISCUS);

        initEmoji();
    }

    public AppComponent getComponent() {
        return component;
    }
}
