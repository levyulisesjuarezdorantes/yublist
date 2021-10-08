package com.yucapps.yuplist.model

data class Genero(val id:Short,val descripcion:String){
    override fun toString(): String {
        return descripcion
    }
}
