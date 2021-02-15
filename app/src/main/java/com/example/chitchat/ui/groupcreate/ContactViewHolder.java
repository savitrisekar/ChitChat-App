package com.example.chitchat.ui.groupcreate;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.ui.adapter.OnItemClickListener;
import com.qiscus.nirmana.Nirmana;

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView itemName;
    private ImageView picture;
    private View viewCheck;

    private OnItemClickListener onItemClickListener;

    public ContactViewHolder(View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);

        itemName = itemView.findViewById(R.id.tv_contact_name);
        picture = itemView.findViewById(R.id.civ_avatar);
        viewCheck = itemView.findViewById(R.id.iv_check);
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
        viewCheck.setVisibility(selectableUser.isSelected() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
