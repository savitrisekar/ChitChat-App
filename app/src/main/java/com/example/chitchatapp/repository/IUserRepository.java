package com.example.chitchatapp.repository;

import com.example.chitchatapp.model.User;
import com.example.chitchatapp.utils.Action;

import java.util.List;

public interface IUserRepository {
    void login(String userId, String password, String displayName, Action<User> onSuccess, Action<Throwable> onError);

    void getUser(Action<List<User>> onSuccess, Action<Throwable> onError);

    void openChat(User user, Action<User> onSuccess, Action<Throwable> onError);

    void updateContacts(List<User> contacts);

    void logout();
}
