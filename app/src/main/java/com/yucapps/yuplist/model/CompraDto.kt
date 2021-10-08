package com.yucapps.yuplist.model

data class CompraDto(val compraId:Long, val nombre:String,val totalCompra:Float, val productos:List<ProductoDto>)
