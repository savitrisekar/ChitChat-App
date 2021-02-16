package com.example.chitchat.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.R;
import com.example.chitchat.ui.qiscus.QiscusChatPresenter;
import com.example.chitchat.ui.qiscus.QiscusSendPhotoConfirmationActivity;
import com.example.chitchat.ui.adapter.CommentsAdapter;
import com.example.chitchat.ui.adapter.QiscusChatScrollListener;
import com.example.chitchat.utils.QiscusPermissionsUtil;
import com.qiscus.sdk.chat.core.data.local.QiscusCacheManager;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;
import com.qiscus.sdk.chat.core.data.model.QiscusPhoto;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;
import com.qiscus.sdk.chat.core.util.QiscusFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatRoomFragment extends Fragment implements QiscusChatPresenter.View,
        QiscusPermissionsUtil.PermissionCallbacks, QiscusChatScrollListener.Listener {

    private static final String CHAT_ROOM_KEY = "extra_chat_room";
    protected static final int SEND_PICTURE_CONFIRMATION_REQUEST = 4;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_FILE_PERMISSION = 2;

    private static final String[] CAMERA_PERMISSION = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
    };

    private static final String[] FILE_PERMISSION = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    private View viewChat;
    private EditText edtMessageField;
    private ImageView ivSendButton, ivAttach;
    private ProgressBar progressBar;
    private RecyclerView rvChat;
    private LinearLayout llEmptyChat, llAttach, llGallery, llCancel, rootViewSender;
    private QiscusChatRoom chatRoom;
    private CommentsAdapter commentsAdapter;
    private QiscusChatPresenter chatPresenter;
    private UserTypingListener userTypingListener;
    private boolean typing;
    private QiscusComment selectedComment = null;

    public static ChatRoomFragment newInstance(QiscusChatRoom chatRoom) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHAT_ROOM_KEY, chatRoom);
        chatRoomFragment.setArguments(bundle);
        return chatRoomFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewChat = inflater.inflate(R.layout.fragment_chat_room, container, false);

        initView();

        return viewChat;
    }

    private void initView() {
        edtMessageField = viewChat.findViewById(R.id.edt_message);
        ivSendButton = viewChat.findViewById(R.id.button_send);
        progressBar = viewChat.findViewById(R.id.progress_bar);
        rvChat = viewChat.findViewById(R.id.rv_room_chat);
        llEmptyChat = viewChat.findViewById(R.id.ll_empty_chat);
        ivAttach = viewChat.findViewById(R.id.btn_attach);
        llGallery = viewChat.findViewById(R.id.ll_gallery);
        llCancel = viewChat.findViewById(R.id.ll_cancel);
        llAttach = viewChat.findViewById(R.id.ll_Attachment);
        rootViewSender = viewChat.findViewById(R.id.rootViewSender);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chatRoom = getArguments().getParcelable(CHAT_ROOM_KEY);
        if (chatRoom == null) {
            throw new RuntimeException("Please provide chat room");
        }

        edtMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                typing = true;
                notifyServerTyping(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSendButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edtMessageField.getText())) {
                if (rootViewSender.getVisibility() == View.VISIBLE) {
                    if (selectedComment != null) {
                        chatPresenter.sendReplyComment(edtMessageField.getText().toString(), selectedComment);
                    }

                    rootViewSender.setVisibility(View.GONE);
                    selectedComment = null;
                } else {
                    chatPresenter.sendComment(edtMessageField.getText().toString());
                }

                edtMessageField.setText("");
            }
        });

        ivAttach.setOnClickListener(v -> {
            if (llAttach.isShown()) {
                hideAttachmentPanel();
            } else {
                showAttachmentPanel();
            }
        });

        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QiscusPermissionsUtil.hasPermissions(getActivity(), FILE_PERMISSION)) {
                    pickImage();
                    hideAttachmentPanel();
                } else {
                    requestReadFilePermission();
                }
            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAttach.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setHasFixedSize(true);
        rvChat.addOnScrollListener(new QiscusChatScrollListener(layoutManager, this));
        commentsAdapter = new CommentsAdapter(getActivity());
        rvChat.setAdapter(commentsAdapter);

        chatPresenter = new QiscusChatPresenter(this, chatRoom);
        chatPresenter.loadComments(20);
    }

    private void hideAttachmentPanel() {
        llAttach.setVisibility(View.GONE);
    }

    private void showAttachmentPanel() {
        llAttach.setVisibility(View.VISIBLE);
    }

    private void notifyServerTyping(boolean typing) {
        QiscusPusherApi.getInstance().publishTyping(chatRoom.getId(), typing);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        userTypingListener = (UserTypingListener) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        QiscusCacheManager.getInstance().setLastChatActivity(true, chatRoom.getId());
        notifyLatestRead();
    }

    @Override
    public void onPause() {
        super.onPause();
        QiscusCacheManager.getInstance().setLastChatActivity(false, chatRoom.getId());
    }

    @Override
    public void showLoadMoreLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void initRoomData(QiscusChatRoom chatRoom, List<QiscusComment> comments) {
        this.chatRoom = chatRoom;
        commentsAdapter.addOrUpdate(comments);

        if (comments.size() == 0) {
            llEmptyChat.setVisibility(View.VISIBLE);
            rvChat.setVisibility(View.GONE);
        } else {
            llEmptyChat.setVisibility(View.GONE);
            rvChat.setVisibility(View.VISIBLE);
        }
        notifyLatestRead();
    }

    @Override
    public void onRoomChanged(QiscusChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void showComments(List<QiscusComment> comments) {
        commentsAdapter.addOrUpdate(comments);
    }

    @Override
    public void onLoadMore(List<QiscusComment> comments) {
        commentsAdapter.addOrUpdate(comments);
    }

    @Override
    public void onSendingComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
        rvChat.smoothScrollToPosition(0);
        if (llEmptyChat.isShown()) {
            llEmptyChat.setVisibility(View.GONE);
            rvChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccessSendComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void onFailedSendComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void onNewComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
        if (((LinearLayoutManager) rvChat.getLayoutManager()).findFirstVisibleItemPosition() <= 2) {
            rvChat.smoothScrollToPosition(0);
        }

        if (llEmptyChat.isShown()) {
            llEmptyChat.setVisibility(View.GONE);
            rvChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCommentDeleted(QiscusComment comment) {
        commentsAdapter.remove(comment);
    }

    @Override
    public void refreshComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void updateLastDeliveredComment(long lastDeliveredCommentId) {
        commentsAdapter.updateLastDeliveredComment(lastDeliveredCommentId);
    }

    @Override
    public void updateLastReadComment(long lastReadCommentId) {
        commentsAdapter.updateLastReadComment(lastReadCommentId);
    }

    @Override
    public void onFileDownloaded(File file, String mimeType) {

    }

    @Override
    public void startPhotoViewer(QiscusComment qiscusComment) {

    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        if (userTypingListener != null) {
            userTypingListener.onUserTyping(user, typing);
        }
    }

    @Override
    public void showCommentsAndScrollToTop(List<QiscusComment> qiscusComments) {

    }

    @Override
    public void onRealtimeStatusChanged(boolean connected) {
        if (connected) {
            QiscusComment comment = commentsAdapter.getLatestSentComment();
            if (comment != null) {
                chatPresenter.loadCommentsAfter(comment);
            }
        }
    }

    @Override
    public void onLoadCommentsError(Throwable throwable) {
        throwable.printStackTrace();
        Log.e("ChatRoomFragment", throwable.getMessage());
    }

    @Override
    public void clearCommentsBefore(long timestamp) {

    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDeleteLoading() {

    }

    @Override
    public void notifyDataChanged() {
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                File imageFile = QiscusFileUtil.from(data.getData());
                List<QiscusPhoto> qiscusPhotos = new ArrayList<>();
                qiscusPhotos.add(new QiscusPhoto(imageFile));
                startActivityForResult(QiscusSendPhotoConfirmationActivity.generateIntent(getActivity(),
                        chatRoom, qiscusPhotos),
                        SEND_PICTURE_CONFIRMATION_REQUEST);
            } catch (Exception e) {
                showError("Failed to open image file!");
            }
        } else if (requestCode == SEND_PICTURE_CONFIRMATION_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError(getString(R.string.display_img_open));
                return;
            }

            Map<String, String> captions = (Map<String, String>)
                    data.getSerializableExtra(QiscusSendPhotoConfirmationActivity.EXTRA_CAPTIONS);
            List<QiscusPhoto> qiscusPhotos = data.getParcelableArrayListExtra(QiscusSendPhotoConfirmationActivity.EXTRA_QISCUS_PHOTOS);
            if (qiscusPhotos != null) {
                for (QiscusPhoto qiscusPhoto : qiscusPhotos) {
                    chatPresenter.sendFile(qiscusPhoto.getPhotoFile(), captions.get(qiscusPhoto.getPhotoFile().getAbsolutePath()));
                }
            } else {
                showError(getString(R.string.display_read_img));
            }
        }
    }

    private void requestReadFilePermission() {
        if (!QiscusPermissionsUtil.hasPermissions(getActivity(), FILE_PERMISSION)) {
            QiscusPermissionsUtil.requestPermissions(this, getString(R.string.qiscus_permission_request_title),
                    REQUEST_FILE_PERMISSION, FILE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        QiscusPermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_FILE_PERMISSION) {
            pickImage();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        QiscusPermissionsUtil.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.qiscus_permission_message),
                R.string.display_grant, R.string.display_denied, perms);
    }

    @Override
    public void onTopOffListMessage() {
        loadMoreComments();
    }

    @Override
    public void onMiddleOffListMessage() {

    }

    @Override
    public void onBottomOffListMessage() {

    }

    private void loadMoreComments() {
        if (progressBar.getVisibility() == View.GONE && commentsAdapter.getItemCount() > 0) {
            QiscusComment comment = commentsAdapter.getData().get(commentsAdapter.getItemCount() - 1);
            if (comment.getId() == -1 || comment.getCommentBeforeId() > 0) {
                chatPresenter.loadOlderCommentThan(comment);
            }
        }
    }

    private void notifyLatestRead() {
        QiscusComment comment = commentsAdapter.getLatestSentComment();
        if (comment != null) {
            QiscusPusherApi.getInstance()
                    .markAsRead(chatRoom.getId(), comment.getId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notifyLatestRead();
        chatPresenter.detachView();
    }

    public interface UserTypingListener {
        void onUserTyping(String user, boolean typing);
    }
}
