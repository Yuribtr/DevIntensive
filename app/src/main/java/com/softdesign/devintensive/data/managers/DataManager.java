package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.GetUserRes;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserLikeRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Picasso;

import okhttp3.MultipartBody;
import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager(){
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevintensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevintensiveApplication.getDaoSession();
    }

    public static DataManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public DataManager(PreferencesManager preferencesManager) {mPreferencesManager = preferencesManager;}

    public PreferencesManager getPreferencesManager() {return mPreferencesManager;}

    public Context getContext(){return mContext;}

    public Picasso getPicasso() {return mPicasso;}

    //region ============ NETWORK ============
    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq){return mRestService.loginUser(userLoginReq);}

    public Call<GetUserRes> getUser (String userId){return mRestService.getUser(userId);}

    public Call<UploadPhotoRes> uploadPhoto (String userId, MultipartBody.Part file){return mRestService.uploadPhoto(userId, file);}

    public Call<UserListRes> getUserListFromNetork() {
        return mRestService.getUserlist();
    }

    public Call<UserLikeRes> setUserLike(String userId) {
        return mRestService.setUserLike(userId);
    }

    public Call<UserLikeRes> setUserUnLike(String userId) { return mRestService.setUserUnLike(userId);}

    //region ============ DATABASE ============

    public DaoSession getDaoSession() {return mDaoSession;}

    //endregion
}