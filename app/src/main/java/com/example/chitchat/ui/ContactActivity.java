package com.example.chitchat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.example.chitchat.ui.adapter.ContactAdapter;
import com.example.chitchat.ui.adapter.OnItemClickListener;
import com.example.chitchat.model.User;
import com.example.chitchat.ui.groupcreate.GroupChatActivity;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactContract.View, OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ContactContract.Presenter presenter;
    private LinearLayout llCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llCreateGroup = findViewById(R.id.ll_create_group_chat);

        recyclerView = findViewById(R.id.rv_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(this);
        contactAdapter.setOnItemClickListener(this);

        recyclerView.setAdapter(contactAdapter);

        presenter = new ContactPresenter(this,
                ChitChatApp.getInstance().getComponent().getUserRepository(),
                ChitChatApp.getInstance().getComponent().getChatRoomRepository());
        presenter.loadContacts(1,100, "");

        llCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupChat();
            }
        });
    }

    private void createGroupChat() {
        Intent intent = new Intent(this, GroupChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        presenter.createRoom(contactAdapter.getData().get(position));
    }

    @Override
    public void showContacts(List<User> contacts) {
        contactAdapter.clear();
        contactAdapter.addOrUpdate(contacts);
    }

    @Override
    public void showChatRoomPage(QiscusChatRoom chatRoom) {
        startActivity(ChatRoomActivity.generateIntent(this, chatRoom));
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}