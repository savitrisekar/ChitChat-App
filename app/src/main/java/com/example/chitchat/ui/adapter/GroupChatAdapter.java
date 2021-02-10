package com.example.chitchat.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.qiscus.nirmana.Nirmana;

import java.util.ArrayList;
import java.util.List;

public class GroupChatAdapter extends SortedRecyclerViewAdapter<GroupChatAdapter.SelectableContact, GroupChatAdapter.Holder> {

    private Context context;

    public GroupChatAdapter(Context context) {
        this.context = context;
    }


    @Override
    protected Class<GroupChatAdapter.SelectableContact> getItemClass() {
        return SelectableContact.class;
    }

    @Override
    protected int compare(GroupChatAdapter.SelectableContact item1, GroupChatAdapter.SelectableContact item2) {
        return item1.user.compareTo(item2.user);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_group_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatAdapter.Holder holder, int position) {
        holder.bind(getData().get(position));
    }

    public void addOrUpdate(List<User> contacts) {
        for (User contact : contacts) {
            SelectableContact selectableContact = new SelectableContact(contact);
            int index = findPosition(selectableContact);
            if (index == -1) {
                getData().add(selectableContact);
            } else {
                selectableContact.selected = getData().get(index).selected;
                getData().updateItemAt(index, selectableContact);
            }
        }
        notifyDataSetChanged();
    }

    public List<User> getSelectedContacts() {
        List<User> contacts = new ArrayList<>();
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).selected) {
                contacts.add(getData().get(i).user);
            }
        }
        return contacts;
    }

    public class Holder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private ImageView avatar;
        private TextView name;
        private CheckBox checkGroup;
//        private View viewCheck;

        private SelectableContact selectableContact;

        public Holder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.civ_avatar);
            name = itemView.findViewById(R.id.tv_contact_name);
            checkGroup = itemView.findViewById(R.id.check_group);

            checkGroup.setOnCheckedChangeListener(this);
        }

        void bind(SelectableContact selectableContact) {
            this.selectableContact = selectableContact;
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_qiscus_avatar)
                            .error(R.drawable.ic_qiscus_avatar)
                            .dontAnimate())
                    .load(selectableContact.user.getAvatarUrl())
                    .into(avatar);
            name.setText(selectableContact.user.getName());
            checkGroup.setSelected(selectableContact.selected);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            selectableContact.selected = isChecked;
        }
    }

    public class SelectableContact implements Parcelable {

        private User user;
        private boolean selected;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public final Creator<SelectableContact> CREATOR = new Creator<SelectableContact>() {
            @Override
            public SelectableContact createFromParcel(Parcel in) {
                return new SelectableContact(in);
            }

            @Override
            public SelectableContact[] newArray(int size) {
                return new SelectableContact[size];
            }
        };

        SelectableContact(User user) {
            this.user = user;
        }

        private SelectableContact(Parcel in) {
            user = in.readParcelable(User.class.getClassLoader());
            selected = in.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return hashCode();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(user, flags);
            dest.writeByte((byte) (selected ? 1 : 0));
        }
    }
}
