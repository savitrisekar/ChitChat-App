package com.example.chitchat.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.R;
import com.example.chitchat.utils.QiscusImageUtil;
import com.qiscus.nirmana.Nirmana;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.io.File;

public class QiscusPhotoFragment extends RxFragment {

    private static final String EXTRA_IMAGE_FILE = "extra_image_file";

    private QiscusTouchImageView imageView;
    private File imageFile;
    private ClickListener listener;

    public static QiscusPhotoFragment newInstance(File imageFile) {
        QiscusPhotoFragment fragment = new QiscusPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_IMAGE_FILE, imageFile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qiscus_photo, container, false);
        imageView = view.findViewById(R.id.image_view);
        imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resolveImageFile(savedInstanceState);
        if (QiscusImageUtil.isImage(imageFile)) {
            Nirmana.getInstance().get()
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .load(imageFile)
                    .into(imageView);
        } else {
            Nirmana.getInstance().get().load(imageFile).into(imageView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof ClickListener) {
            listener = (ClickListener) activity;
        }
    }

    private void resolveImageFile(Bundle savedInstanceState) {
        imageFile = (File) getArguments().getSerializable(EXTRA_IMAGE_FILE);
        if (imageFile == null && savedInstanceState != null) {
            imageFile = savedInstanceState.getParcelable(EXTRA_IMAGE_FILE);
        }

        if (imageFile == null) {
            getActivity().finish();
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_IMAGE_FILE, imageFile);
    }

    public interface ClickListener {
        void onPhotoClick();
    }
}
