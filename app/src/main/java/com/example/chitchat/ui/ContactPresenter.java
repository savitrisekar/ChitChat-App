package com.example.chitchat.ui;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.ChatRoomRepository;
import com.example.chitchat.repository.UserRepository;

public class ContactPresenter implements ContactContract.Presenter {

    private ContactContract.View view;
    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;

    public ContactPresenter(ContactContract.View view, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.view = view;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public void loadContacts() {
        userRepository.getUser(users -> view.showContacts(users),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void createRoom(User contact) {
        chatRoomRepository.createChatRoom(contact,
                chatRoom -> view.showChatRoomPage(chatRoom),
                throwable -> view.showErrorMessage(throwable.getMessage()));

        userRepository.openChat(contact, intent -> view.showRoomPage(intent),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }
}
