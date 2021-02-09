package com.example.chitchat.repository;

import android.content.Intent;

import com.example.chitchat.model.User;
import com.example.chitchat.utils.Action;

import java.util.List;

public interface UserRepository {
    void login(String userId, String password, String displayName, Action<User> onSuccess, Action<Throwable> onError);
    void getUser(Action<List<User>> onSuccess, Action<Throwable> onError);
    void openChat(User user, Action<Intent> onSuccess, Action<Throwable> onError);
    void updateContacts(List<User> contacts);
    void logout();
}
