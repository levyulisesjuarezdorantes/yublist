package com.yucapps.yuplist

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.yucapps.yuplist.adapter.RecyclerViewAdapterCompras
import com.yucapps.yuplist.databinding.ActivityComprasBinding
import com.yucapps.yuplist.databinding.ActivityRegistroBinding
import com.yucapps.yuplist.model.CompraDto
import com.yucapps.yuplist.model.CompraHeaderDto
import com.yucapps.yuplist.repository.UserRepository
import com.yucapps.yuplist.request.CompraUsuarioRequestDto
import com.yucapps.yuplist.response.MensajeOkResponseDto
import com.yucapps.yuplist.utils.ApiService
import com.yucapps.yuplist.utils.RetrofitUtils
import com.yucapps.yuplist.viewmodel.ComprasUsuarioViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ComprasActivity : AppCompatActivity() , ComprasListener {
    lateinit var binding : ActivityComprasBinding
    private  lateinit var deviceId:String
    lateinit var recyclerViewAdapter: RecyclerViewAdapterCompras
    private lateinit var viewModel: ComprasUsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deviceId = intent.getStringExtra("dispositivoId").toString()
        initReclyclerView()
        initViewModel()
        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
        initAdMob()
        this.title = "Mis Listas de compras"
    }
    fun showDialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Lista de compra")
        val input = EditText(this)
        input.setHint("Escribe nombre de tu lista")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var nulo = input.text.isNullOrBlank()
            if(!nulo)
                createCompra(input.text.toString())

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.setCancelable(false)
        builder.show()
    }
    fun createCompra(nombreList:String){
        viewModel.comprasObservable
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            UserRepository::class.java).CreateCompra(deviceId, CompraUsuarioRequestDto(nombreList))
        call.enqueue(object: Callback<CompraDto> {
            override fun onResponse(call: Call<CompraDto>, response: Response<CompraDto>) {
                if(response.isSuccessful){
                      val compraDto = response.body()!!
                      recyclerViewAdapter.comprasUsuario.add(CompraHeaderDto(compraDto.compraId,compraDto.nombre,0.0f,0))
                      recyclerViewAdapter.notifyDataSetChanged()
                }else{
                    if(response.errorBody()!=null){
                        Toast.makeText(this@ComprasActivity,response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CompraDto>, t: Throwable) {
                Toast.makeText(this@ComprasActivity,"Ocurrio un error  "+ t.stackTrace, Toast.LENGTH_LONG).show()

            }

        })

    }
    private fun initReclyclerView(){
        binding.rvCompras.apply {
            layoutManager = LinearLayoutManager(this@ComprasActivity)
            val decoration = DividerItemDecoration(this@ComprasActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapterCompras(this@ComprasActivity)
            recyclerViewAdapter.deviceId = deviceId
            adapter = recyclerViewAdapter

        }
    }
    private fun initAdMob(){
        val adRequest = AdRequest.Builder().build()
        binding.adViewCompras.loadAd(adRequest)
    }
    override fun onResume() {
        viewModel.loadCompras(this@ComprasActivity)
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    fun initViewModel(){
        viewModel = ViewModelProvider(this)[ComprasUsuarioViewModel::class.java]
        viewModel.deviceId = deviceId
        viewModel.loadCompras(this@ComprasActivity)
        viewModel.comprasObservable.observe(this, Observer<List<CompraHeaderDto>>{
            if(it==null){
                Toast.makeText(this@ComprasActivity,"Ya puede Agregar listas para comprars", Toast.LENGTH_LONG).show()
            }else{
                if(it.size==0)
                    Toast.makeText(this@ComprasActivity,"Ya puede Agregar listas para comprar", Toast.LENGTH_LONG).show()
                recyclerViewAdapter.comprasUsuario = it.toMutableList()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onLongClick(compraId: Long) {
        val compra =  recyclerViewAdapter.comprasUsuario.filter { it.compraId==compraId }.first()
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            UserRepository::class.java).DeleteCompra(deviceId,compraId)
        call.enqueue(object: Callback<MensajeOkResponseDto> {
            override fun onResponse(call: Call<MensajeOkResponseDto>, response: Response<MensajeOkResponseDto>) {
                if(response.isSuccessful){

                val result =  recyclerViewAdapter.comprasUsuario.remove(compra)
                recyclerViewAdapter.notifyDataSetChanged()

                 }
            }

            override fun onFailure(call: Call<MensajeOkResponseDto>, t: Throwable) {
                Toast.makeText(this@ComprasActivity,"Ocurrio un error  "+ t.stackTrace, Toast.LENGTH_LONG).show()
            }

        })
    }
}