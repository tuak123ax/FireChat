package com.example.firechat.notification;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Client {
    private static Retrofit retrofit=null;

    public static Retrofit getClient(String url)
    {
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder().baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
