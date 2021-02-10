package com.example.chitchat.ui;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.UserRepository;

import java.util.List;

public class GroupChatPresenter implements GroupChatContract.Presenter {

    private GroupChatContract.View view;
    private UserRepository userRepository;

    public GroupChatPresenter(GroupChatContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }

    @Override
    public void loadContacts() {
        userRepository.getUser(
                users -> view.showContacts(users),
                throwable -> view.showErrorMessage(throwable.getMessage())
        );
    }

    @Override
    public void selectContacts(List<User> selectedContacts) {
        if (selectedContacts.isEmpty()) {
            view.showErrorMessage("Please select at least one contact!");
            return;
        }
        view.showCreateGroupPage(selectedContacts);
    }
}
