package com.example.chitchat.ui.groupcreate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.example.chitchat.ui.groupcreate.newgroup.NewGroupFragment;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements GroupChatContract.View {

    private RecyclerView rvGroup, rvSelect;
    private ContactAdapter contactAdapter;
    private GroupChatContract.Presenter presenter;
    private SelectedContactAdapter selectedContactAdapter;
    private ImageView ivNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        rvGroup = findViewById(R.id.rv_group);
        rvGroup.setLayoutManager(new LinearLayoutManager(this));
        rvGroup.setHasFixedSize(true);

        ivNext = findViewById(R.id.iv_forward);

        contactAdapter = new ContactAdapter(this, position -> {
            presenter.selectContact(contactAdapter.getData().get(position));
        });
        rvGroup.setAdapter(contactAdapter);

        rvSelect = findViewById(R.id.rv_select);
        rvSelect.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelect.setHasFixedSize(true);

        selectedContactAdapter = new SelectedContactAdapter(this, position -> {
            presenter.selectContact(selectedContactAdapter.getData().get(position));
        });
        rvSelect.setAdapter(selectedContactAdapter);

        ivNext.setOnClickListener(v -> {
            if (selectedContactIsMoreThanOne()) {
                List<User> contacts = new ArrayList<>();
                int size = selectedContactAdapter.getData().size();
                for (int i = 0; i < size; i++) {
                    contacts.add(selectedContactAdapter.getData().get(i).getUser());
                }

                Fragment fragment = NewGroupFragment.newInstance(contacts);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout, fragment)
                        .addToBackStack("tag")
                        .commit();
            } else {
                Toast.makeText(this, "select at least one", Toast.LENGTH_SHORT).show();
            }
        });

        presenter = new GroupChatPresenter(this, ChitChatApp.getInstance().getComponent().getUserRepository());
        presenter.loadContacts(1,100, "");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean selectedContactIsMoreThanOne() {
        return selectedContactAdapter.getData().size() > 0;
    }

    @Override
    public void onBackPressed() {
        onReturn();
    }

    private void onReturn() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !currentFragment.isVisible()) {
            finish();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
    }

    @Override
    public void showContacts(List<SelectableUser> contacts) {
        contactAdapter.clear();
        contactAdapter.addOrUpdate(contacts);
    }

    @Override
    public void onSelectedContactChange(SelectableUser contact) {
        contactAdapter.addOrUpdate(contact);
        if (contact.isSelected()) {
            selectedContactAdapter.addOrUpdate(contact);
        } else {
            selectedContactAdapter.remove(contact);
        }

        rvSelect.setVisibility(selectedContactAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}