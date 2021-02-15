package com.example.chitchat.repository;

import android.content.Intent;

import com.example.chitchat.model.User;
import com.example.chitchat.utils.Action;

import java.util.List;

public interface UserRepository {
    void login(String email, String password, String name, Action<User> onSuccess, Action<Throwable> onError);

    void getCurrentUser(Action<User> onSuccess, Action<Throwable> onError);

    void getUsers(long page, int limit, String query, Action<List<User>> onSuccess, Action<Throwable> onError);

    void updateProfile(String name, Action<User> onSuccess, Action<Throwable> onError);

    void logout();
}
