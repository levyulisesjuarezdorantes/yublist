package com.yucapps.yuplist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.yucapps.yuplist.response.CountryResponseDto;

import java.util.List;

public class RegistroUsuarioViewModel extends  ViewModel {
    private String deviceId;
    private Boolean userRegistered;
    private List<CountryResponseDto> countries;

    private boolean triedUser;
    private boolean triedCountries;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public void setUserRegistered(Boolean userRegistered) {
        this.userRegistered = userRegistered;
    }

    public Boolean getUserRegistered() {
        return userRegistered;
    }

    public List<CountryResponseDto> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryResponseDto> countries) {
        this.countries = countries;
    }

    public boolean getTriedUser() {
        return triedUser;
    }

    public void setTriedUser(boolean triedUser) {
        this.triedUser = triedUser;
    }

    public boolean getTriedCountries() {
        return triedCountries;
    }

    public void setTriedCountries(boolean triedCountries) {
        this.triedCountries = triedCountries;
    }
}
