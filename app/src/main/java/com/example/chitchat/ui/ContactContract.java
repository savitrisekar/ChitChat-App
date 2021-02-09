package com.example.chitchat.ui;

import android.content.Intent;

import com.example.chitchat.model.User;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class ContactContract {
    interface View {
        void showContacts(List<User> contacts);

        void showChatRoomPage(QiscusChatRoom chatRoom);

        void showRoomPage(Intent intent);

        void showErrorMessage(String errorMessage);
    }

    interface Presenter {
        void loadContacts();

        void createRoom(User contact);
    }
}
