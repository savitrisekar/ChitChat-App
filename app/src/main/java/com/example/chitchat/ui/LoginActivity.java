package com.example.chitchat.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.qiscus.sdk.chat.core.QiscusCore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    private EditText edtPassword, edtDisplayName, edtUserID;
    private Button btnLogin;
    private LoginPresenter loginPresenter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (QiscusCore.hasSetupUser()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        loginPresenter = new LoginPresenter(this, ChitChatApp.getInstance().getCompat().getUserRepository());

        initView();
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        edtUserID = findViewById(R.id.userId);
        edtPassword = findViewById(R.id.password);
        edtDisplayName = findViewById(R.id.dispayName);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_login) {
            if (TextUtils.isEmpty(edtDisplayName.getText().toString())) {
                edtDisplayName.setError("Must not Empty!");
                edtDisplayName.requestFocus();
            } else if (TextUtils.isEmpty(edtUserID.getText().toString())) {
                edtUserID.setError("Must not Empty!");
                edtUserID.requestFocus();
            } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                edtPassword.setError("Must not Empty!");
                edtPassword.requestFocus();
            } else {
                loginPresenter.login(
                        edtUserID.getText().toString().trim(),
                        edtPassword.getText().toString().trim(),
                        edtDisplayName.getText().toString().trim());
            }
        }
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showHomePage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}