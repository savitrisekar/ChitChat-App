package com.example.chitchat;

import android.app.Application;
import android.os.Build;
import com.example.chitchat.utils.Constant;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.QiscusCore;

public class ChitChatApp extends Application {
    private static ChitChatApp INSTANCE;
    private AppComponent compat;

    public static ChitChatApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        compat = new AppComponent(this);
        Nirmana.init(this);

        Qiscus.init(this, Constant.APP_ID_QISCUS);

        Qiscus.getChatConfig()
                .setSwipeRefreshColorScheme(R.color.colorPrimary, R.color.colorAccent)
                .setLeftBubbleColor(R.color.leftBubble)
                .setLeftBubbleTextColor(R.color.qiscus_primary_text)
                .setLeftBubbleTimeColor(R.color.qiscus_secondary_text)
                .setLeftLinkTextColor(R.color.qiscus_primary_text)
                .setLeftProgressFinishedColor(R.color.colorPrimary)
                .setRightBubbleColor(R.color.colorPrimaryLight)
                .setRightProgressFinishedColor(R.color.colorPrimary)
                .setSelectedBubbleBackgroundColor(R.color.colorPrimary)
                .setReadIconColor(R.color.colorPrimary)
                .setAppBarColor(R.color.colorPrimary)
                .setStatusBarColor(R.color.colorPrimaryDark)
                .setAccentColor(R.color.colorAccent)
                .setAccountLinkingTextColor(R.color.colorPrimary)
                .setAccountLinkingBackground(R.color.accountLinkingBackground)
                .setButtonBubbleTextColor(R.color.colorPrimary)
                .setButtonBubbleBackBackground(R.color.accountLinkingBackground)
                .setReplyBarColor(R.color.colorPrimary)
                .setReplySenderColor(R.color.colorPrimaryLight)
                .setSendButtonIcon(R.drawable.ic_qiscus_send)
                .setStopRecordIcon(R.drawable.ic_qiscus_send_off)
                .setCancelRecordIcon(R.drawable.ic_cancel_circle)
                .setInlineReplyColor(R.color.colorPrimary);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Qiscus.getChatConfig().setEnableReplyNotification(true);
        }
    }

    public AppComponent getCompat() {
        return compat;
    }
}
