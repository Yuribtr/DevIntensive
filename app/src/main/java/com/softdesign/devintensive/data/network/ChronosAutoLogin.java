package com.softdesign.devintensive.data.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.UserModelManager;
import com.softdesign.devintensive.data.network.res.GetUserRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Response;

public class ChronosAutoLogin extends ChronosOperation<String> {
    private DataManager mDataManager;
    private Context mContext;
    private String result="null";

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public String run() {
        //имитируем медленную сеть
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        mContext= DevintensiveApplication.getContext();
        mDataManager = DataManager.getInstance();
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            String userId = mDataManager.getPreferencesManager().getUserId();
            String authToken = mDataManager.getPreferencesManager().getAuthToken();
            if (userId.isEmpty() || authToken.isEmpty()) {
                result = mContext.getString(R.string.error_credintials_not_found_message);
            } else {
                Call<GetUserRes> call = mDataManager.getUser(userId);
                try {
                    Response<GetUserRes> response = call.execute();
                    if (response.code() == 200) {
                        UserModelManager.saveUserModelToPreferenses(mDataManager, response.body());
                        result = mContext.getString(R.string.success_token_message);
                    } else if (response.code() == 404) {
                        result = mContext.getString(R.string.error_login_or_password);
                    } else if (response.code() == 401) {
                        result = mContext.getString(R.string.error_token_message);
                    } else {
                        result = mContext.getString(R.string.error_unknown);
                    }
                } catch (Exception e) {
                    result = e.toString();
                }
            }
        } else {
            result = mContext.getString(R.string.error_network_is_not_available);
        }
        return result;
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