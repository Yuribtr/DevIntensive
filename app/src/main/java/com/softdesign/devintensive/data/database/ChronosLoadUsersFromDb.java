package com.softdesign.devintensive.data.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.UiHelper;

import org.greenrobot.greendao.query.WhereCondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChronosLoadUsersFromDb extends ChronosOperation<List<User>> {
    private DaoSession mDaoSession;
    private String mLikeQuery="";

    public ChronosLoadUsersFromDb(String likeQuery) {
        mLikeQuery = likeQuery;
    }

    public ChronosLoadUsersFromDb() {

    }

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public List<User> run() {
        UiHelper.writeLog("ChronosLoadUsersFromDb called");
        mDaoSession = DevintensiveApplication.getDaoSession();
        List<User> userList = new ArrayList<>();
        WhereCondition likeWhere;
        if (mLikeQuery.isEmpty())
            likeWhere = UserDao.Properties.SearchName.like("%");
        else
            likeWhere = UserDao.Properties.SearchName.like("%" + mLikeQuery.toUpperCase() + "%");
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(likeWhere)
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();

            //вручную инициализируем поле своего лайка
            String mMyUserId = DataManager.getInstance().getPreferencesManager().getUserId();
            for (int i=0; i<userList.size();i++) {
                userList.get(i).setIsMyLike(false);
                for (int k=0; k<userList.get(i).getUserLiked().size(); k++) {
                    if (userList.get(i).getUserLiked().get(k).getUserLiked().equals(mMyUserId)) {
                        userList.get(i).setIsMyLike(true);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<List<User>>> getResultClass() {
        return Result.class;
    }

    // the class is a named version of ChronosOperationResult<> generic class
    // it is required because Java disallows method overriding by using generic class with another parameter
    // and result delivery is based on calling particular methods with the exact same result class
    // later we'll see how Chronos use this class
    public final static class Result extends ChronosOperationResult<List<User>> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}