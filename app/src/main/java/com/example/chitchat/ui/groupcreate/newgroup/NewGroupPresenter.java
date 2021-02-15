package com.example.chitchat.ui.groupcreate.newgroup;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.ChatRoomRepository;

import java.util.List;

public class NewGroupPresenter implements NewGroupContract.Presenter {
    private NewGroupContract.View view;
    private ChatRoomRepository chatRoomRepository;

    public NewGroupPresenter(NewGroupContract.View view, ChatRoomRepository chatRoomRepository) {
        this.view = view;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void createGroup(String name, List<User> members) {
        view.showLoading();
        chatRoomRepository.createGroupChatRoom(name, members,
                qiscusChatRoom -> {
                    view.dismissLoading();
                    view.showGroupChatRoomPage(qiscusChatRoom);
                },
                throwable -> {
                    view.dismissLoading();
                    view.showErrorMessage(throwable.getMessage());
                }
        );
    }
}
