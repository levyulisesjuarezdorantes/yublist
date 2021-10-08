package com.yucapps.yuplist.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

public class RetrofitUtils {

    companion object{
        public fun getRetrofit(url:String): Retrofit {
            return Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
        }

    }

}