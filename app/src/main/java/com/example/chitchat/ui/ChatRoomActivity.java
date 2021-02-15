package com.example.chitchat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;
import com.qiscus.sdk.chat.core.data.model.QiscusRoomMember;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;
import com.qiscus.sdk.chat.core.event.QiscusUserStatusEvent;
import com.qiscus.sdk.chat.core.util.QiscusDateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.Observable;

public class ChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.UserTypingListener {

    private static final String CHAT_ROOM_KEY = "extra_chat_room";

    private QiscusChatRoom chatRoom;
    private String opponentEmail;
    private TextView tvRoom, tvName;
    private ImageView ivAvatar, btnBack;

    public static Intent generateIntent(Context context, QiscusChatRoom chatRoom) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(CHAT_ROOM_KEY, chatRoom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatRoom = getIntent().getParcelableExtra(CHAT_ROOM_KEY);
        if (chatRoom == null) {
            finish();
            return;
        }

        initView();

        Nirmana.getInstance().get()
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .dontAnimate())
                .load(chatRoom.getAvatarUrl())
                .into(ivAvatar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        ChatRoomFragment.newInstance(chatRoom),
                        ChatRoomFragment.class.getName())
                .commit();

        getOpponentIfNotGroupEmail();

        listenUser();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        tvRoom = findViewById(R.id.tv_detile_chat);
        ivAvatar = findViewById(R.id.civ_avatar);
        tvName = findViewById(R.id.tv_name);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvName.setText(chatRoom.getName());
    }

    private void getOpponentIfNotGroupEmail() {
        if (!chatRoom.isGroup()) {
            opponentEmail = Observable.from(chatRoom.getMember())
                    .map(QiscusRoomMember::getEmail)
                    .filter(email -> !email.equals(QiscusCore.getQiscusAccount().getEmail()))
                    .first()
                    .toBlocking()
                    .single();
        }
    }

    private ChatRoomFragment getChatFragment() {
        return (ChatRoomFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onDestroy() {
        unlistenUser();
        super.onDestroy();
    }

    private void listenUser() {
        if (!chatRoom.isGroup() && opponentEmail != null) {
            QiscusPusherApi.getInstance().subscribeUserOnlinePresence(opponentEmail);
        }
    }

    private void unlistenUser() {
        if (!chatRoom.isGroup() && opponentEmail != null) {
            QiscusPusherApi.getInstance().unsubscribeUserOnlinePresence(opponentEmail);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onUserStatusChanged(QiscusUserStatusEvent event) {
        String last = QiscusDateUtil.getRelativeTimeDiff(event.getLastActive());
        tvRoom.setText(event.isOnline() ? "Online" : "Last seen " + last);
        tvRoom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        tvRoom.setText(typing ? "Typing..." : "Online");
        tvRoom.setVisibility(View.VISIBLE);
    }
}