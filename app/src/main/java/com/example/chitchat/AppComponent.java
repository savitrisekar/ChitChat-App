package com.example.chitchat;

import android.content.Context;

import com.example.chitchat.repository.ChatRoomRepository;
import com.example.chitchat.repository.UserRepository;
import com.example.chitchat.repository.impl.ChatRoomRepositoryImpl;
import com.example.chitchat.repository.impl.UserRepositoryImpl;

public class AppComponent {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    AppComponent(Context context) {
        userRepository = new UserRepositoryImpl(context);
        chatRoomRepository = new ChatRoomRepositoryImpl();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ChatRoomRepository getChatRoomRepository() {
        return chatRoomRepository;
    }
}
