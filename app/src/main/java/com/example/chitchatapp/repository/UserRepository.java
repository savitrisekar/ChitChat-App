package com.example.chitchatapp.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.example.chitchatapp.R;
import com.example.chitchatapp.model.User;
import com.example.chitchatapp.utils.Action;
import com.example.chitchatapp.utils.AvatarUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusAccount;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserRepository implements IUserRepository {

    private Context context;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    public UserRepository(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("rooms", Context.MODE_PRIVATE);
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @Override
    public void login(String userId, String password, String displayName, Action<User> onSuccess, Action<Throwable> onError) {
        QiscusCore.setUser(userId, password)
                .withUsername(displayName)
                .withAvatarUrl(AvatarUtil.generateAvatar(displayName))
                .save()
                .map(this::mapFromQiscusAccount)
                .doOnNext(this::setCurrentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void getUser(Action<List<User>> onSuccess, Action<Throwable> onError) {
        getUsersObservable()
                .flatMap(Observable::from)
                .filter(user -> !user.equals(getCurrentUser()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void openChat(User user, Action<User> onSuccess, Action<Throwable> onError) {
        QiscusCore.updateUserAsObservable(user.getId(), getCurrentUser().getAvatarUrl())
                .map(this::mapFromQiscusAccount)
                .doOnNext(this::setCurrentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void updateContacts(List<User> contacts) {
        sharedPreferences.edit()
                .putString("contacts", gson.toJson(contacts))
                .apply();
    }

    @Override
    public void logout() {
        QiscusCore.clearUser();
        sharedPreferences.edit().clear().apply();
    }

    private User mapFromQiscusAccount(QiscusAccount qiscusAccount) {
        User user = new User();
        user.setId(qiscusAccount.getEmail());
        user.setName(qiscusAccount.getUsername());
        user.setAvatarUrl(qiscusAccount.getAvatar());
        return user;
    }

    private Observable<List<User>> getUsersObservable() {
        return Observable.create(subscriber -> {
            try {
                List<User> users = gson.fromJson(getUsersData(), new TypeToken<List<User>>() {
                }.getType());
                subscriber.onNext(users);
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private String getUsersData() throws IOException, JSONException {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.users);

        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return new String(bytes);
    }

    private User getCurrentUser() {
        return gson.fromJson(sharedPreferences.getString("current_user", ""), User.class);
    }

    private void setCurrentUser(User user) {
        sharedPreferences.edit()
                .putString("current_user", gson.toJson(user))
                .apply();
    }
}
