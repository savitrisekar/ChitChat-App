package com.example.chitchatapp.utils;

import com.example.chitchatapp.model.User;

public interface Action<T> {
    void call(T t);

//    void call(User user);
}
