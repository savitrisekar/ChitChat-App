package com.example.chitchat.ui.groupcreate.newgroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.ChitChatApp;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.example.chitchat.ui.GroupChatRoomActivity;
import com.example.chitchat.ui.MainActivity;
import com.example.chitchat.ui.adapter.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.ArrayList;
import java.util.List;

public class NewGroupFragment extends Fragment implements NewGroupContract.View {

    private static final String CONTACT_KEY = "CONTACT_KEY";
    private static final String selectMore = "select at least one";
    private static final String groupNameFormat = "Please input group name";

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private EditText groupNameView;
    private FloatingActionButton fabSubmit;

    private NewGroupContract.Presenter presenter;

    private List<User> contacts;
    private NewGroupAdapter adapter;

    public static NewGroupFragment newInstance(List<User> contacts) {
        NewGroupFragment fragment = new NewGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CONTACT_KEY, (ArrayList<User>) contacts);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        View view = inflater.inflate(R.layout.fragment_new_group, container, false);

        recyclerView = view.findViewById(R.id.rv_participants);
        groupNameView = view.findViewById(R.id.edt_subject_name);
        fabSubmit = view.findViewById(R.id.fab_submit);

        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedCreateGroup();
            }
        });

        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contacts = getArguments().getParcelableArrayList(CONTACT_KEY);
        if (contacts == null) {
            getActivity().finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new NewGroupAdapter(getActivity(), new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                adapter.remove(contacts.get(position));
            }
        });
        adapter.needRemoveParticipant(true);

        recyclerView.setAdapter(adapter);
        adapter.addOrUpdate(contacts);

        presenter = new NewGroupPresenter(this, ChitChatApp.getInstance().getComponent().getChatRoomRepository());
    }

    public void proceedCreateGroup() {
        String groupName = groupNameView.getText().toString();
        boolean groupNameInputted = groupName.trim().length() > 0;
        if (groupNameInputted && selectedContactIsMoreThanOne()) {
            presenter.createGroup(groupName, contacts);
        } else {
            String warningText = (groupNameInputted) ? selectMore : groupNameFormat;
            showErrorMessage(warningText);
        }
    }

    private boolean selectedContactIsMoreThanOne() {
        return this.contacts.size() > 0;
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showGroupChatRoomPage(QiscusChatRoom chatRoom) {
        Intent chatIntent = GroupChatRoomActivity.generateIntent(getContext(), chatRoom);
        Intent parentIntent = new Intent(getActivity(), MainActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
        taskStackBuilder.addNextIntentWithParentStack(parentIntent);
        taskStackBuilder.addNextIntent(chatIntent);
        taskStackBuilder.startActivities();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
