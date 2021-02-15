package com.example.chitchat.ui.groupcreate;

import com.example.chitchat.model.User;
import com.example.chitchat.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupChatPresenter implements GroupChatContract.Presenter {
    private GroupChatContract.View view;
    private UserRepository userRepository;
    private List<SelectableUser> contacts;

    public GroupChatPresenter(GroupChatContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
        contacts = new ArrayList<>();
    }

    public void loadContacts(long page, int limit, String query) {
        userRepository.getUsers(page, limit, query, users -> {
            for (User user : users) {
                SelectableUser selectableUser = new SelectableUser(user);
                int index = contacts.indexOf(selectableUser);
                if (index >= 0) {
                    contacts.get(index).setUser(user);
                } else {
                    contacts.add(selectableUser);
                }
            }
            view.showContacts(contacts);
        }, throwable -> {
            view.showErrorMessage(throwable.getMessage());
        });
    }

    public void search(String keyword) {
        Observable.from(contacts)
                .filter(user -> user.getUser().getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> view.showContacts(users), throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public void selectContact(SelectableUser contact) {
        int index = contacts.indexOf(contact);
        if (index >= 0) {
            contacts.get(index).setSelected(!contacts.get(index).isSelected());
            view.onSelectedContactChange(contacts.get(index));
        }
    }
}
