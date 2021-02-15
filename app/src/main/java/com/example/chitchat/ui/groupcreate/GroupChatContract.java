package com.example.chitchat.ui.groupcreate;

import java.util.List;

public class GroupChatContract {
    interface View {
        void showContacts(List<SelectableUser> contacts);

        void onSelectedContactChange(SelectableUser contact);

        void showErrorMessage(String errorMessage);
    }

    interface Presenter {
        void loadContacts(long page, int limit, String query);

        void selectContact(SelectableUser contact);

        void search(String keyword);
    }
}
