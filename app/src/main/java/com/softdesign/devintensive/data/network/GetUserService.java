package com.softdesign.devintensive.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import com.softdesign.devintensive.data.network.res.GetUserRes;

public interface GetUserService {

    @GET("user/{userId}")
    Call<GetUserRes> getUser (@Path("userId") String userId);


}
