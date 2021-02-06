package com.example.chitchatapp.repository;

import com.example.chitchatapp.model.User;
import com.example.chitchatapp.utils.Action;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public interface IChatRoomRepository {
    void getChatRooms(Action<List<QiscusChatRoom>> onSuccess, Action<Throwable> onError);

    void createChatRoom(User user, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);

    void createGroupChatRoom(String name, List<User> members, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);
}
