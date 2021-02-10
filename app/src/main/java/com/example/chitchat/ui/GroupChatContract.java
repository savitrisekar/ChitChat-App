package com.example.chitchat.ui;

import com.example.chitchat.model.User;

import java.util.List;

public class GroupChatContract {
    interface View {
        void showContacts(List<User> contacts);
        void showCreateGroupPage(List<User> members);
        void showErrorMessage(String errorMessage);
    }

    interface Presenter {
        void loadContacts();
        void selectContacts(List<User> selectedContacts);
    }
}
