package com.example.chitchat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.example.chitchat.ui.adapter.ContactAdapter;
import com.example.chitchat.ui.adapter.GroupChatAdapter;
import com.example.chitchat.ui.adapter.OnItemClickListener;

import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements GroupChatContract.View {

    private RecyclerView rvGroup;
    private GroupChatAdapter groupChatAdapter;
    private GroupChatContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvGroup = findViewById(R.id.rv_group);
        rvGroup.setLayoutManager(new LinearLayoutManager(this));
        rvGroup.setHasFixedSize(true);

        groupChatAdapter = new GroupChatAdapter(this);
        rvGroup.setAdapter(groupChatAdapter);

        findViewById(R.id.tv_next).setOnClickListener(v -> submit());

        presenter = new GroupChatPresenter(this,
                ChitChatApp.getInstance().getCompat().getUserRepository());
        presenter.loadContacts();
    }

    private void submit() {
        presenter.selectContacts(groupChatAdapter.getSelectedContacts());
    }

    @Override
    public void showContacts(List<User> contacts) {
        groupChatAdapter.addOrUpdate(contacts);
    }

    @Override
    public void showCreateGroupPage(List<User> members) {
        finish();
        startActivity(CreateGroupActivity.generateIntent(this, members));
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}