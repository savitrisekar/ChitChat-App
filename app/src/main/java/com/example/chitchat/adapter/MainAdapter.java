package com.example.chitchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.utils.DateUtil;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;

import java.util.List;

public class MainAdapter extends SortedRecyclerViewAdapter<QiscusChatRoom, MainAdapter.ViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;

    public MainAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected Class<QiscusChatRoom> getItemClass() {
        return QiscusChatRoom.class;
    }

    @Override
    protected int compare(QiscusChatRoom item1, QiscusChatRoom item2) {
        return item2.getLastComment().getTime().compareTo(item1.getLastComment().getTime());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getData().get(position));
    }

    public void addOrUpdate(List<QiscusChatRoom> chatRooms) {
        for (QiscusChatRoom chatRoom : chatRooms) {
            int index = findPosition(chatRoom);
            if (index == -1) {
                getData().add(chatRoom);
            } else {
                getData().updateItemAt(index, chatRoom);
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAvatar;
        private TextView tvName, tvLastMessage, tvUnread, tvTime;
        private OnItemClickListener onItemClickListener;
        private FrameLayout flUnread;

        ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            ivAvatar = view.findViewById(R.id.civ_image);
            tvName = view.findViewById(R.id.tv_username);
            tvLastMessage = view.findViewById(R.id.tv_last_msg);
            tvUnread = view.findViewById(R.id.tv_unread);
            tvTime = view.findViewById(R.id.tv_time);
            flUnread = view.findViewById(R.id.fl_unread_count);

            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @SuppressLint("ResourceAsColor")
        void bind(QiscusChatRoom chatRoom) {

            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_qiscus_avatar)
                            .error(R.drawable.ic_qiscus_avatar)
                            .dontAnimate())
                    .load(chatRoom.getAvatarUrl())
                    .into(ivAvatar);
            tvName.setText(chatRoom.getName());
            QiscusComment lastComment = chatRoom.getLastComment();
            if (lastComment != null && lastComment.getId() > 0) {
                if (lastComment.getSender() != null) {
                    String lastMessageText = lastComment.isMyComment() ? "You: " : lastComment.getSender().split(" ")[0] + ": ";
                    lastMessageText += chatRoom.getLastComment().getType() == QiscusComment.Type.IMAGE
                            ? "\uD83D\uDCF7 send an image" : lastComment.getMessage();
                    tvLastMessage.setText(lastMessageText);
                } else {
                    String lastMessageText = "";
                    lastMessageText += chatRoom.getLastComment().getType() == QiscusComment.Type.IMAGE
                            ? "\uD83D\uDCF7 send an image" : lastComment.getMessage();
                    tvLastMessage.setText(lastMessageText);
                }

                tvTime.setText(DateUtil.toTimeDate(chatRoom.getLastComment().getTime()));
            } else {
                tvLastMessage.setText("");
                tvTime.setText("");
            }

            tvUnread.setText(String.format("%d", chatRoom.getUnreadCount()));
            if (chatRoom.getUnreadCount() == 0) {
                flUnread.setVisibility(View.GONE);
            } else {
                flUnread.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
