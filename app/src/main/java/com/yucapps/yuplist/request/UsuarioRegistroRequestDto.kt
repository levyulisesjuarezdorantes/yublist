package com.yucapps.yuplist.request

data class UsuarioRegistroRequestDto(
    val nombreUsuario:String,
    val password:String,
    val edad:Short,
    val paisId:Int,
    val genero:Short,
    val nombre:String,
    val dispositivoId:String
)
