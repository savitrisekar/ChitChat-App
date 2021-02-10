package com.example.chitchat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.ui.QiscusChannelActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements CreateGroupContract.View {

    private static final String GROUP_KEY = "groups";

    private ProgressDialog progressDialog;
    private EditText edtSubject;
    private CreateGroupContract.Presenter presenter;

    public static Intent generateIntent(Context context, List<User> members) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        intent.putParcelableArrayListExtra(GROUP_KEY, (ArrayList<User>) members);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        List<User> members = getIntent().getParcelableArrayListExtra(GROUP_KEY);
        if (members == null) {
            finish();
            return;
        }

        initView();

        presenter = new CreateGroupPresenter(this,
                ChitChatApp.getInstance().getCompat().getChatRoomRepository());

        findViewById(R.id.create_submit).setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtSubject.getText().toString())) {
                edtSubject.setError("Please set group name!");
            } else {
                presenter.createGroup(edtSubject.getText().toString(), members);
            }
        });
    }

    private void initView() {
        edtSubject = findViewById(R.id.edt_subject_group);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait!");

        findViewById(R.id.create_cancel).setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void showGroupChatPage(QiscusChatRoom qiscusChatRoom) {
        finish();
        startActivity(QiscusChannelActivity.generateIntent(this, qiscusChatRoom));
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}