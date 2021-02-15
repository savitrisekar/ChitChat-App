package com.example.chitchat.ui.groupcreate.newgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.model.User;
import com.example.chitchat.ui.adapter.OnItemClickListener;
import com.example.chitchat.ui.adapter.SortedRecyclerViewAdapter;
import com.qiscus.nirmana.Nirmana;

public class NewGroupAdapter extends SortedRecyclerViewAdapter<User, NewGroupAdapter.Holder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isRemoved;

    public NewGroupAdapter(Context context, OnItemClickListener onItemClickListener) {
        super();
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected Class<User> getItemClass() {
        return User.class;
    }

    @Override
    protected int compare(User item1, User item2) {
        return item1.getName().compareTo(item2.getName());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false), onItemClickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(getData().get(position));
        holder.needRemoveParticipant(isRemoved);
    }

    public void needRemoveParticipant(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private ImageView ivPicture, ivRemove;
        private boolean isRemovedParticipant;

        private OnItemClickListener onItemClickListener;

        public Holder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);

            tvName = itemView.findViewById(R.id.tv_contact_name);
            ivPicture = itemView.findViewById(R.id.civ_avatar);
            ivRemove = itemView.findViewById(R.id.iv_cancel_contact);
        }

        public void bind(User user) {
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .dontAnimate())
                    .load(user.getAvatarUrl())
                    .into(ivPicture);

            if (isRemovedParticipant) {
                ivRemove.setVisibility(View.GONE);
            } else {
                ivRemove.setVisibility(View.VISIBLE);
            }

            tvName.setText(user.getName());
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }

        public void needRemoveParticipant(boolean isRemoved) {
            this.isRemovedParticipant = isRemoved;
        }
    }
}
