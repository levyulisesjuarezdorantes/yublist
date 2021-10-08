package com.yucapps.yuplist.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yucapps.yuplist.model.CompraDto;
import com.yucapps.yuplist.model.ProductoDto;
import com.yucapps.yuplist.repository.UserRepository;
import com.yucapps.yuplist.response.MensajeOkResponseDto;
import com.yucapps.yuplist.utils.ApiService;
import com.yucapps.yuplist.utils.RetrofitUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductosUsuarioViewModel extends ViewModel {

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId;

    public long getCompraId() {
        return compraId;
    }

    public void setCompraId(long compraId) {
        this.compraId = compraId;
    }

    private long compraId;
    private MutableLiveData<List<ProductoDto>> productosList;

    public ProductosUsuarioViewModel(){
        productosList = new MutableLiveData<>();
    }
    public MutableLiveData<List<ProductoDto>> getProductosObservable(){
        return productosList;
    }

    public void loadProductos(Context context){
        Retrofit retrofit =  RetrofitUtils.Companion.getRetrofit(ApiService.getUrl(context));
        Call<CompraDto> call =  retrofit.create(UserRepository.class).GetProductos(deviceId,compraId);
        call.enqueue(new Callback<CompraDto>() {
            @Override
            public void onResponse(Call<CompraDto> call, Response<CompraDto> response) {
                if(response.isSuccessful()){
                     List<ProductoDto> productos = response.body().getProductos();
                     if(productos!=null)
                            productosList.postValue(productos);
                }

            }

            @Override
            public void onFailure(Call<CompraDto> call, Throwable t) {
                productosList.postValue(null);
            }
        });
    }

    public void deleteProducts(Context context,@NotNull String deviceId, long compraId) {
        Retrofit retrofit =  RetrofitUtils.Companion.getRetrofit(ApiService.getUrl(context));
        Call<MensajeOkResponseDto> call =  retrofit.create(UserRepository.class).DeleteProductos(deviceId,compraId);
        call.enqueue(new Callback<MensajeOkResponseDto>() {
            @Override
            public void onResponse(Call<MensajeOkResponseDto> call, Response<MensajeOkResponseDto> response) {
                if(response.isSuccessful()){
                     productosList.postValue(new ArrayList<>());

                }

            }

            @Override
            public void onFailure(Call<MensajeOkResponseDto> call, Throwable t) {
                productosList.postValue(null);
            }
        });
    }
}
