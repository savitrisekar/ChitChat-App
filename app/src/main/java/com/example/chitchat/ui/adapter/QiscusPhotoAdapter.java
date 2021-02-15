package com.example.chitchat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.utils.QiscusImageUtil;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.data.model.QiscusPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QiscusPhotoAdapter extends RecyclerView.Adapter<QiscusPhotoAdapter.Holder> {

    private Context context;
    private List<QiscusPhoto> qiscusPhotos;

    private OnItemClickListener onItemClickListener;

    public QiscusPhotoAdapter(Context context) {
        this.context = context;
        qiscusPhotos = new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_qiscus_photo, parent, false),
                onItemClickListener);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        File imageFile = qiscusPhotos.get(position).getPhotoFile();
        if (QiscusImageUtil.isImage(imageFile)) {
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .load(imageFile)
                    .into(holder.imageView);
        } else {
            Nirmana.getInstance().get().load(imageFile).into(holder.imageView);
        }

        if (qiscusPhotos.get(position).isSelected()) {
            holder.imageView.setBackground(null);
        } else {
            holder.imageView.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return qiscusPhotos.size();
    }

    public List<QiscusPhoto> getQiscusPhotos() {
        return qiscusPhotos;
    }

    public void refreshWithData(List<QiscusPhoto> qiscusPhotos) {
        this.qiscusPhotos.clear();
        this.qiscusPhotos.addAll(qiscusPhotos);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateSelected(int position) {
        for (int i = 0; i < qiscusPhotos.size(); i++) {
            qiscusPhotos.get(i).setSelected(i == position);
        }
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;

        private OnItemClickListener itemClickListener;

        public Holder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.itemClickListener = itemClickListener;
            imageView = itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
