package com.yucapps.yuplist.repository

import com.yucapps.yuplist.response.CountryResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

public interface CountryRepository {
    @GET()
    public  fun  GetAll(@Url url:String): Call<List<CountryResponseDto>>;

}