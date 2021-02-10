package com.example.chitchat.ui;

import com.example.chitchat.model.User;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class CreateGroupContract {

    interface Presenter{
        void createGroup(String name, List<User> members);
    }
    interface View {
        void showGroupChatPage(QiscusChatRoom qiscusChatRoom);
        void showLoading();
        void hideLoading();
        void showErrorMessage(String errorMessage);
    }
}
