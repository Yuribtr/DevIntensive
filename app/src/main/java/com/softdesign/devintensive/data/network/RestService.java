package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.GetUserRes;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestService {

    @Headers({
            "Custom-Header : YuriBtr"
    })
    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);

    @GET ("user/list?orderBy=rating")
    Call<UserListRes> getUserlist();

    @GET("user/{userId}")
    Call<GetUserRes> getUser (@Path("userId") String userId);

    @Multipart
    @POST("user/{userId}/publicValues/profilePhoto")
    Call<UploadPhotoRes> uploadPhoto(@Path("userId") String userId,
                                     @Part MultipartBody.Part file);

}
