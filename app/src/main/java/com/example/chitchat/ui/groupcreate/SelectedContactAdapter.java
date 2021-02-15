package com.example.chitchat.ui.groupcreate;

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
import com.example.chitchat.ui.adapter.OnItemClickListener;
import com.example.chitchat.ui.adapter.SortedRecyclerViewAdapter;
import com.qiscus.nirmana.Nirmana;

public class SelectedContactAdapter extends SortedRecyclerViewAdapter<SelectableUser, SelectedContactAdapter.Holder> {
    private Context context;
    private OnItemClickListener onItemClickListener;

    public SelectedContactAdapter(Context context, OnItemClickListener onItemClickListener) {
        super();
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected Class<SelectableUser> getItemClass() {
        return SelectableUser.class;
    }

    @Override
    protected int compare(SelectableUser item1, SelectableUser item2) {
        return item1.getUser().getName().compareTo(item2.getUser().getName());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(context).inflate(R.layout.item_select_contact, parent, false), onItemClickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(getData().get(position));
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemName;
        private ImageView picture;

        private OnItemClickListener onItemClickListener;

        public Holder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);

            itemName = itemView.findViewById(R.id.tv_name);
            picture = itemView.findViewById(R.id.civ_avatar);
        }

        public void bind(SelectableUser selectableUser) {
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .dontAnimate())
                    .load(selectableUser.getUser().getAvatarUrl())
                    .into(picture);

            itemName.setText(selectableUser.getUser().getName());
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
