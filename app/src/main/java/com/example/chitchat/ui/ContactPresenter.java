package com.example.chitchat.ui;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.ChatRoomRepository;
import com.example.chitchat.repository.UserRepository;

import java.util.List;

public class ContactPresenter implements ContactContract.Presenter {

    private ContactContract.View view;
    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;
    private List<User> users;

    public ContactPresenter(ContactContract.View view, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.view = view;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void loadContacts(long page, int limit, String query) {
        userRepository.getUsers(page, limit, query, users -> {
            view.showContacts(users);
            this.users = users;
        }, throwable -> {
            view.showErrorMessage(throwable.getMessage());
        });
    }

    public void createRoom(User contact) {
        chatRoomRepository.createChatRoom(contact,
                chatRoom -> view.showChatRoomPage(chatRoom),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }
}
