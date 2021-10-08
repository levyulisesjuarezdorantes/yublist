package com.yucapps.yuplist.model

data class ComprasUsuarioResponseDto(val nombreUsuario:String, val dispositivoId:String, val compras:List<CompraHeaderDto>) {
}