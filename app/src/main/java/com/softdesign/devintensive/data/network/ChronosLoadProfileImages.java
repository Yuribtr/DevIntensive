package com.softdesign.devintensive.data.network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.ui.activities.ProfileUserActivity;
import com.softdesign.devintensive.ui.activities.UserListActivity;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import org.greenrobot.greendao.query.WhereCondition;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChronosLoadProfileImages extends ChronosOperation<UsersAdapter> {
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private Context mContext;

    public ChronosLoadProfileImages(List<User> users, Context context) {
        this.mUsers = users;
        this.mContext = context;
    }

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public UsersAdapter run() {
        try {
            mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
                @Override
                public void onUserItemClickListener(int position) {
                    //передаем данные профиля конкретного пользователя в новую активити через Data Transfer Objects
                    UserDTO userDTO = new UserDTO(mUsers.get(position));
                    Intent profileIntent = new Intent(mContext, ProfileUserActivity.class);
                    profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                    //переходим в просмотр чужого профиля
                    mContext.startActivity(profileIntent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mUsersAdapter;
    }

    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<UsersAdapter>> getResultClass() {
        return Result.class;
    }

    // the class is a named version of ChronosOperationResult<> generic class
    // it is required because Java disallows method overriding by using generic class with another parameter
    // and result delivery is based on calling particular methods with the exact same result class
    // later we'll see how Chronos use this class
    public final static class Result extends ChronosOperationResult<UsersAdapter> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}