package com.yucapps.yuplist.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.yucapps.yuplist.model.CompraDto;
import com.yucapps.yuplist.model.CompraHeaderDto;
import com.yucapps.yuplist.model.ComprasUsuarioResponseDto;
import com.yucapps.yuplist.repository.UserRepository;
import com.yucapps.yuplist.utils.ApiService;
import com.yucapps.yuplist.utils.RetrofitUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ComprasUsuarioViewModel extends ViewModel {

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId;
    private MutableLiveData<List<CompraHeaderDto>> comprasList;

  
    public ComprasUsuarioViewModel(){
        comprasList = new MutableLiveData<>();
    }
    public MutableLiveData<List<CompraHeaderDto>> getComprasObservable(){
        return comprasList;
    }
    public void loadCompras(Context context){
        Retrofit retrofit =  RetrofitUtils.Companion.getRetrofit(ApiService.getUrl(context));
        Call<ComprasUsuarioResponseDto> comprasCall =  retrofit.create(UserRepository.class).GetCompras(deviceId);
        comprasCall.enqueue(new Callback<ComprasUsuarioResponseDto>() {
            @Override
            public void onResponse(Call<ComprasUsuarioResponseDto> call, Response<ComprasUsuarioResponseDto> response) {
                if(response.isSuccessful()){
                    comprasList.postValue(response.body().getCompras());
                }

            }

            @Override
            public void onFailure(Call<ComprasUsuarioResponseDto> call, Throwable t) {
                comprasList.postValue(null);
            }
        });
    }
}
