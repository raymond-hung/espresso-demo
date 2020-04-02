package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EchoService {
    @GET("/get")
    Call<String> getEchoResponse();

}
