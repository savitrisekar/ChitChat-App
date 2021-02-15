package com.example.chitchat.repository;

import com.example.chitchat.model.User;
import com.example.chitchat.utils.Action;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public interface ChatRoomRepository {
    void getChatRooms(Action<List<QiscusChatRoom>> onSuccess, Action<Throwable> onError);

    void createChatRoom(User user, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);

    void createGroupChatRoom(String name, List<User> members, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);

    void addParticipant(long roomId, List<User> members, Action<Void> onSuccess, Action<Throwable> onError);
}
