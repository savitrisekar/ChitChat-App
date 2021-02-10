package com.example.chitchat.ui;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.ChatRoomRepository;

import java.util.List;

public class CreateGroupPresenter implements CreateGroupContract.Presenter {

    private CreateGroupContract.View view;
    private ChatRoomRepository chatRoomRepository;

    public CreateGroupPresenter(CreateGroupContract.View view, ChatRoomRepository chatRoomRepository) {
        this.view = view;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public void createGroup(String name, List<User> members) {
        view.showLoading();
        chatRoomRepository.createGroupChatRoom(name, members,
                qiscusChatRoom -> {
                    view.hideLoading();
                    view.showGroupChatPage(qiscusChatRoom);
                },
                throwable -> {
                    view.hideLoading();
                    view.showErrorMessage(throwable.getMessage());
                });
    }
}
