package com.yucapps.yuplist.utils;

import android.content.Context;

import com.yucapps.yuplist.R;

public class ApiService {
    public static String getUrl(Context context){
        String result =  context.getString(R.string.url_api);
        return result;
    }
}
