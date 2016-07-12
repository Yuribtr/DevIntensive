package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RestService {

    @Headers({
            "Custom-Header : my header"
    })
    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);




//    @Headers({
//            "Custom-Header : my header"
//    })
//    @POST("login")
//    Call<UserModelRes> loginUser (@Header("Last-Modified") String lastMod,
//                                  @Body UserLoginReq req);

}
