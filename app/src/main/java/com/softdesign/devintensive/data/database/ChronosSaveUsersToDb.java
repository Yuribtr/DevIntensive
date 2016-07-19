package com.softdesign.devintensive.data.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.UserModelManager;
import com.softdesign.devintensive.data.network.GetUserService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.res.GetUserRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChronosSaveUsersToDb extends ChronosOperation<String> {
    private DataManager mDataManager;
    private DaoSession mDaoSession;
    private Context mContext;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;
    private String result="null";

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public String run() {
        //имитируем длительное сохранение пользователей
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        mContext=DevintensiveApplication.getContext();
        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<UserListRes> call = mDataManager.getUserListFromNetork();
            try {
                Response<UserListRes> response = call.execute();
                if (response.code()==200) {
                    List<Repository> allRepositories = new ArrayList<Repository>();
                    List<User> allUsers = new ArrayList<User>();
                    for (UserListRes.Datum  userRes : response.body().getData()) {
                        //сохраняем список репозиториев каждого пользователя в общий список
                        allRepositories.addAll(getRepoListFromUserRes(userRes));
                        //сохраняем каждого пользователя в общий список
                        allUsers.add(new User(userRes));
                    }
                    mRepositoryDao.insertOrReplaceInTx(allRepositories);
                    mUserDao.insertOrReplaceInTx(allUsers);
                    result=mContext.getString(R.string.success_user_list_load);
                } else if (response.code()==404) {
                    result=mContext.getString(R.string.error_login_or_password);
                } else if (response.code()==401) {
                    result=mContext.getString(R.string.error_token_message);
                } else {
                    result=mContext.getString(R.string.error_unknown);
                }
            } catch (Exception e) {
                result=e.toString();
            }
        } else {
            result=mContext.getString(R.string.error_network_is_not_available);
        }
        return result;
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.Datum userData){
        final String userId = userData.getId();
        List<Repository> repositories = new ArrayList<>();
        for (UserListRes.Datum.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }

    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return Result.class;
    }

    // the class is a named version of ChronosOperationResult<> generic class
    // it is required because Java disallows method overriding by using generic class with another parameter
    // and result delivery is based on calling particular methods with the exact same result class
    // later we'll see how Chronos use this class
    public final static class Result extends ChronosOperationResult<String> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}