package com.example.chitchat.ui.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.ui.fragment.ChatRoomFragment;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusRoomMember;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.UserTypingListener,
        QiscusApi.MetaRoomParticipantsListener {

    private static final String CHAT_ROOM_KEY = "extra_chat_room";

    private TextView membersView, roomName;
    private ImageView avatar;
    private QiscusChatRoom chatRoom;
    private String subtitle;

    public static Intent generateIntent(Context context, QiscusChatRoom chatRoom) {
        Intent intent = new Intent(context, GroupChatRoomActivity.class);
        intent.putExtra(CHAT_ROOM_KEY, chatRoom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_room);

        chatRoom = getIntent().getParcelableExtra(CHAT_ROOM_KEY);
        if (chatRoom == null) {
            finish();
            return;
        }
        setParticipants();

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
        avatar = findViewById(R.id.civ_avatar);
        roomName = findViewById(R.id.tv_name);
        membersView = findViewById(R.id.tv_detile_chat);

        Nirmana.getInstance().get()
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .dontAnimate())
                .load(chatRoom.getAvatarUrl())
                .into(avatar);
        roomName.setText(chatRoom.getName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        ChatRoomFragment.newInstance(chatRoom),
                        ChatRoomFragment.class.getName())
                .commit();

//        membersView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(RoomInfoActivity.generateIntent(getApplication(), chatRoom));
//                finish();
//            }
//        });

//        roomName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(RoomInfoActivity.generateIntent(getApplication(), chatRoom));
//                finish();
//            }
//        });
    }

    private ChatRoomFragment getChatFragment() {
        return (ChatRoomFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void setParticipants() {
        QiscusApi.getInstance().getParticipants(chatRoom.getUniqueId(), 1, 100, null, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(participants -> {
                    chatRoom.setMember(participants);
                    membersView.setText(generateSubtitle(chatRoom.getMember()));
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private String generateSubtitle(List<QiscusRoomMember> members) {
        if (!TextUtils.isEmpty(subtitle)) {
            return subtitle;
        }
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (QiscusRoomMember member : members) {
            if (!member.getEmail().equalsIgnoreCase(QiscusCore.getQiscusAccount().getEmail())) {
                count++;
                builder.append(member.getUsername().split(" ")[0]);
                if (count < members.size() - 1) {
                    builder.append(", ");
                }
            }
            if (count >= 10) {
                break;
            }
        }
        builder.append(String.format(" %s", getString(R.string.qiscus_group_member_closing)));
        if (count == 0) {
            builder = new StringBuilder(getString(R.string.qiscus_group_member_only_you));
        }
        subtitle = builder.toString();
        return subtitle;
    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        if (typing) {
            QiscusRoomMember member = findMember(user);
            if (member != null) {
                membersView.setText(member.getUsername().split(" ")[0] + " is typing...");
            } else {
                membersView.setText(generateSubtitle(chatRoom.getMember()));
            }
        } else {
            membersView.setText(generateSubtitle(chatRoom.getMember()));
        }
    }

    private QiscusRoomMember findMember(String userId) {
        for (QiscusRoomMember member : chatRoom.getMember()) {
            if (member.getEmail().equals(userId)) {
                return member;
            }
        }
        return null;
    }

    @Override
    public void onMetaReceived(int currentPage, int perPage, int total) {

    }
}