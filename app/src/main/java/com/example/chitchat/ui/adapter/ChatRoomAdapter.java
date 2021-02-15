package com.example.chitchat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.utils.DateUtil;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;

import java.util.List;

public class ChatRoomAdapter extends SortedRecyclerViewAdapter<QiscusChatRoom, ChatRoomAdapter.Holder>  {

    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatRoomAdapter(Context context) {
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

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new Holder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
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

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivAvatar;
        private TextView tvName, tvTime, tvUnread;
        private com.vanniktech.emoji.EmojiTextView eLastMessage;
        private FrameLayout layout_unread_count;
        private OnItemClickListener onItemClickListener;

        Holder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.civ_image);
            tvName = itemView.findViewById(R.id.tv_username);
            eLastMessage = itemView.findViewById(R.id.tv_last_msg);
            tvUnread = itemView.findViewById(R.id.tv_unread);
            tvTime = itemView.findViewById(R.id.tv_time);
            layout_unread_count = itemView.findViewById(R.id.fl_unread_count);

            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        void bind(QiscusChatRoom chatRoom) {
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
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
                    eLastMessage.setText(lastMessageText);
                }else{
                    String lastMessageText = "";
                    lastMessageText += chatRoom.getLastComment().getType() == QiscusComment.Type.IMAGE
                            ? "\uD83D\uDCF7 send an image" : lastComment.getMessage();
                    eLastMessage.setText(lastMessageText);
                }

                tvTime.setText(DateUtil.getLastMessageTimestamp(lastComment.getTime()));
            } else {
                eLastMessage.setText("");
                tvTime.setText("");
            }

            tvUnread.setText(String.format("%d", chatRoom.getUnreadCount()));
            if (chatRoom.getUnreadCount() == 0) {
                layout_unread_count.setVisibility(View.GONE);
            } else {
                layout_unread_count.setVisibility(View.VISIBLE);
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
