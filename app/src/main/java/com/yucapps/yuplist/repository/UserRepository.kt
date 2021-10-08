package com.yucapps.yuplist.repository

import com.yucapps.yuplist.model.CompraDto
import com.yucapps.yuplist.model.ComprasUsuarioResponseDto
import com.yucapps.yuplist.model.ProductoDto
import com.yucapps.yuplist.request.CompraUsuarioRequestDto
import com.yucapps.yuplist.request.ProductoCompraRequestDto
import com.yucapps.yuplist.request.UsuarioRegistroRequestDto
import com.yucapps.yuplist.response.MensajeOkResponseDto
import retrofit2.Call
import retrofit2.http.*

public interface UserRepository {

    @POST("/usuarios")
    public fun Register(@Body request: UsuarioRegistroRequestDto): Call<String>

    @GET("/usuarios/{dispositivoId}")
    public fun Get(@Path(value = "dispositivoId")dispositivoId:String): Call<Void>

    @GET("/usuarios/{dispositivoId}/compras")
    public fun GetCompras(@Path(value="dispositivoId")dispositivoId: String): Call<ComprasUsuarioResponseDto>

    @POST("/usuarios/{dispositivoId}/compras/{compraId}/productos")
    public fun CreateProducto(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long, @Body request: ProductoCompraRequestDto): Call<ProductoDto>

    @POST("/usuarios/{dispositivoId}/compras")
    public fun CreateCompra(@Path(value="dispositivoId")dispositivoId: String, @Body request: CompraUsuarioRequestDto): Call<CompraDto>

    @GET("/usuarios/{dispositivoId}/compras/{compraId}/productos")
    public fun GetProductos(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long): Call<CompraDto>

    @PUT("/usuarios/{dispositivoId}/compras/{compraId}/productos/{productoId}")
    public fun UpdateProducto(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long,@Path(value = "productoId")productoId:Long,@Body request: ProductoDto): Call<MensajeOkResponseDto>

    @DELETE("/usuarios/{dispositivoId}/compras/{compraId}/productos")
    public fun DeleteProductos(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long): Call<MensajeOkResponseDto>

    @DELETE("/usuarios/{dispositivoId}/compras/{compraId}/productos/{productoId}")
    public fun DeleteProducto(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long,@Path(value = "productoId")productoId:Long): Call<MensajeOkResponseDto>

    @DELETE("/usuarios/{dispositivoId}/compras/{compraId}")
    public fun DeleteCompra(@Path(value="dispositivoId")dispositivoId: String, @Path(value = "compraId")compraId:Long): Call<MensajeOkResponseDto>

}