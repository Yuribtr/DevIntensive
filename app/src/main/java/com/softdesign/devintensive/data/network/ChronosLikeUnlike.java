package com.softdesign.devintensive.data.network;

import android.content.Context;
import android.support.annotation.NonNull;
import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserLikeRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.LikesBy;
import com.softdesign.devintensive.data.storage.models.LikesByDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.UiHelper;

import org.greenrobot.greendao.internal.DaoConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChronosLikeUnlike extends ChronosOperation<UserLikeRes> {
    private DataManager mDataManager;
    private Context mContext;
    private String result="null";
    private String mUserId;
    private boolean mIsLike;
    private UserLikeRes mUserLikeRes=null;
    private LikesByDao mLikesByDao;
    private DaoSession mDaoSession;

    public ChronosLikeUnlike(String userId, boolean isLike) {
        mIsLike = isLike;
        mUserId = userId;
    }

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public UserLikeRes run() {
        UiHelper.writeLog("ChronosLikeUnlike called");
        mContext= DevintensiveApplication.getContext();
        mDataManager = DataManager.getInstance();
        mLikesByDao = mDataManager.getDaoSession().getLikesByDao();
        mDaoSession = DevintensiveApplication.getDaoSession();

//// TODO: 27.07.2016 remove this
        mLikesByDao.detachAll();
        DataManager.getInstance().getDaoSession().getUserDao().detachAll();

        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<UserLikeRes> call;
            if (mIsLike)
                call = mDataManager.setUserLike(mUserId);
            else
                call = mDataManager.setUserUnLike(mUserId);
            try {
                Response<UserLikeRes> response = call.execute();
                mUserLikeRes = response.body();
                if (response.code() == 200) {
                    //создаем обновленный список лайков
                    List<LikesBy> allLikes = new ArrayList<LikesBy>();
                    for (String userLikeBy : response.body().getData().likesBy) {
                        LikesBy userLike = new LikesBy(userLikeBy, mUserId);
                        allLikes.add(userLike);
                    }
                    List oldLikes =  mDaoSession.queryBuilder(LikesBy.class).where(LikesByDao.Properties.UserRemoteId.like(mUserId)).list();
                    if (oldLikes!=null && oldLikes.size()>0) {
                        mDaoSession.getLikesByDao().deleteInTx(oldLikes);
                    }
                    mDaoSession.getLikesByDao().insertOrReplaceInTx(allLikes);
                    //возвращаем корректную строку результата
                    if (mIsLike) {
                        result = mContext.getString(R.string.success_like_message);
                    } else {
                        result = mContext.getString(R.string.success_unlike_message);
                    }
                    mUserLikeRes.data.setResponseMessage(result);
                } else if (response.code() == 404) {
                    result = mContext.getString(R.string.error_login_or_password);
                    mUserLikeRes.data.setResponseMessage(result);
                } else if (response.code() == 401) {
                    result = mContext.getString(R.string.error_token_message);
                    mUserLikeRes.data.setResponseMessage(result);
                } else {
                    result = mContext.getString(R.string.error_unknown);
                    mUserLikeRes.data.setResponseMessage(result);
                }
            } catch (Exception e) {
                result = e.toString();
                mUserLikeRes.data.setResponseMessage(result);
            }
        } else {
            result=mContext.getString(R.string.error_network_is_not_available);
            mUserLikeRes.data.setResponseMessage(result);
        }
        return mUserLikeRes;
    }


    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<UserLikeRes>> getResultClass() {
        return Result.class;
    }

    // the class is a named version of ChronosOperationResult<> generic class
    // it is required because Java disallows method overriding by using generic class with another parameter
    // and result delivery is based on calling particular methods with the exact same result class
    // later we'll see how Chronos use this class
    public final static class Result extends ChronosOperationResult<UserLikeRes> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}