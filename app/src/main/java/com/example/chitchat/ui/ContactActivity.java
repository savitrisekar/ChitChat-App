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
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements OnItemClickListener, ContactContract.View {

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


        recyclerView = findViewById(R.id.rv_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(this);
        contactAdapter.setOnItemClickListener(this);

        recyclerView.setAdapter(contactAdapter);

        initView();

        presenter = new ContactPresenter(this,
                ChitChatApp.getInstance().getCompat().getUserRepository(),
                ChitChatApp.getInstance().getCompat().getChatRoomRepository());

        presenter.loadContacts();
    }

    private void initView() {
        llCreateGroup = findViewById(R.id.ll_create_group_chat);

        llCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactActivity.this, GroupChatActivity.class));
                Toast.makeText(ContactActivity.this, "halo", Toast.LENGTH_SHORT).show();
            }
        });
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

    }

    @Override
    public void showRoomPage(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}