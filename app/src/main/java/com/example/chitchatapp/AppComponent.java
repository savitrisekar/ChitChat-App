package com.example.chitchatapp;

import android.content.Context;

import com.example.chitchatapp.repository.ChatRoomRepository;
import com.example.chitchatapp.repository.IChatRoomRepository;
import com.example.chitchatapp.repository.UserRepository;
import com.example.chitchatapp.repository.IUserRepository;

public class AppComponent {
    private final IUserRepository userRepo;
    private final IChatRoomRepository chatRoomRepo;

    public AppComponent(Context context) {
        userRepo = new UserRepository(context);
        chatRoomRepo = new ChatRoomRepository();
    }

    public IUserRepository getUserRepository() {
        return userRepo;
    }

    public IChatRoomRepository getChatRoomRepository() {
        return chatRoomRepo;
    }
}
