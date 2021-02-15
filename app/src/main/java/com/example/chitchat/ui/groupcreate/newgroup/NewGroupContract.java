package com.example.chitchat.ui.groupcreate.newgroup;

import com.example.chitchat.model.User;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class NewGroupContract  {

    interface View {
        void showLoading();

        void dismissLoading();

        void showGroupChatRoomPage(QiscusChatRoom chatRoom);

        void showErrorMessage(String errorMessage);
    }

    interface Presenter {
        void createGroup(String name, List<User> members);
    }
}
