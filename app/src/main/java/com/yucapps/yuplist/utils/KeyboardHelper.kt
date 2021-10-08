package com.yucapps.yuplist.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardHelper {
   companion object{
       fun hide(view:View){
           (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken,0)
       }
   }


}