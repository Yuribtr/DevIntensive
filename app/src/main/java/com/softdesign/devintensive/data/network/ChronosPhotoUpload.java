package com.softdesign.devintensive.data.network;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ChronosPhotoUpload extends ChronosOperation<String> {
    private DataManager mDataManager;
    private Context mContext;
    private UserModelRes userModel;
    private String result="null";
    private Uri mFileUri;
    private byte mMode;
    private String mUserId;

    public ChronosPhotoUpload(String userId, Uri fileUri, byte mode) {
        mFileUri = fileUri;
        mMode = mode;
        mUserId = userId;
    }

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public String run() {
        mContext= DevintensiveApplication.getContext();
        mDataManager = DataManager.getInstance();
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {

            FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
            File file=null;
            switch (mMode) {
                case 1://если фото пришло с галлереи то преобразуем его путь
                    file = new File(getRealPathFromURI(mContext, mFileUri));
                    break;
                case 2://если с камеры то напрямую подставляем
                    file = new File(mFileUri.getPath());
                    break;
            }
            if (file==null) {
                result = mContext.getString(R.string.user_profile_image_file_error);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
                Call<UploadPhotoRes> call = service.uploadPhoto(mUserId, body);
                try {
                    Response<UploadPhotoRes> response = call.execute();
                    if (response.code() == 200) {
                        result = mContext.getString(R.string.success_photo_update_message);
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
            result=mContext.getString(R.string.error_network_is_not_available);
        }
        return result;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
