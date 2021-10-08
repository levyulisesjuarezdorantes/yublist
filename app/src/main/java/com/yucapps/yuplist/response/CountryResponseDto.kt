package com.yucapps.yuplist.response

data class CountryResponseDto(val paisId: Int,val nombre:String) {
    override fun toString(): String {
        return nombre;
    }
}
