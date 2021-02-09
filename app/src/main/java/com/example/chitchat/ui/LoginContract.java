package com.example.chitchat.ui;

public class LoginContract {
    interface View {
        void showHomePage();
        void showErrorMessage(String errorMessage);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void login(String userId, String password, String displayName);
    }
}
