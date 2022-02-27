package com.yucapps.yuplist

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yucapps.yuplist.adapter.RecyclerViewAdapterCompras
import com.yucapps.yuplist.adapter.RecyclerViewAdapterProductos
import com.yucapps.yuplist.databinding.ActivityComprasBinding
import com.yucapps.yuplist.databinding.ActivityProductosBinding
import com.yucapps.yuplist.model.ProductoDto
import com.yucapps.yuplist.repository.UserRepository
import com.yucapps.yuplist.request.CompraUsuarioRequestDto
import com.yucapps.yuplist.request.ProductoCompraRequestDto
import com.yucapps.yuplist.response.MensajeOkResponseDto
import com.yucapps.yuplist.utils.ApiService
import com.yucapps.yuplist.utils.RetrofitUtils
import com.yucapps.yuplist.viewmodel.ComprasUsuarioViewModel
import com.yucapps.yuplist.viewmodel.ProductosUsuarioViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProductosActivity : AppCompatActivity() , ProductoCheckedListener {
    lateinit var binding : ActivityProductosBinding
    private lateinit var viewModel: ProductosUsuarioViewModel
    private  lateinit var deviceId : String
    private  var compraId:Long = 0
    lateinit var recycleView: RecyclerViewAdapterProductos

    private var interstitialAd:InterstitialAd? = null
    private var showInsterstitial:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deviceId = intent.getStringExtra("deviceId").toString()
        compraId = intent.getLongExtra("compraId",-1)
        initViewModel()
        initReclyclerView()
        initButtonAddProductoActions()
        initAdMob()
        initInterstitialAd()
        initInterstitialListener()
    }
    private fun initInterstitialListener(){
        interstitialAd?.fullScreenContentCallback = object:FullScreenContentCallback(){
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }
    }
    private fun initAdMob(){
        val adRequest = AdRequest.Builder().build()
        binding.adViewProductos.loadAd(adRequest)
    }
    private fun initInterstitialAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,getString(R.string.ad_mob_interstitial),adRequest,object:InterstitialAdLoadCallback(){
            override fun onAdLoaded(p0: InterstitialAd) {
               interstitialAd = p0
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                interstitialAd = null
            }
        })
    }
    private fun showInterstitialValid():Boolean{
        if(showInsterstitial) {
            showInsterstitial = false
            return true
        }else{
            showInsterstitial = true
            return false
        }

    }

    private fun showInterstitial(){
        initInterstitialAd()
        interstitialAd?.show(this)
    }
    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
    fun createProducto(productName:String){
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            UserRepository::class.java).CreateProducto(deviceId,compraId, ProductoCompraRequestDto(productName))
        call.enqueue(object: Callback<ProductoDto> {
            override fun onResponse(call: Call<ProductoDto>, response: Response<ProductoDto>) {
                if(response.isSuccessful){
                    val result = response.body()!!
                    recycleView.productosUsuario.add(result)
                    recycleView.notifyDataSetChanged();

                }else{
                    if(response.errorBody()!=null){
                        Toast.makeText(this@ProductosActivity,response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ProductoDto>, t: Throwable) {
                Toast.makeText(this@ProductosActivity,"Ocurrio un error "+ t.stackTrace, Toast.LENGTH_LONG).show()

            }

        })

    }
    private fun initButtonAddProductoActions(){
        var addProductaImageButton =  binding.imgAddProducto
        // textViewTotal = binding.txvTotal
        addProductaImageButton.isEnabled = false
        var editTextNameButton =binding.edtProductName

        addProductaImageButton.setOnClickListener {
            createProducto(editTextNameButton.text.trim().toString())
            editTextNameButton.text.clear()
        }
        editTextNameButton.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        editTextNameButton.afterTextChanged {
            if(isValidText(it)){
                addProductaImageButton.isEnabled = true
                addProductaImageButton.setBackgroundColor(resources.getColor(R.color.primaryDarkColor))


            }else{
                addProductaImageButton.isEnabled = false
                addProductaImageButton.setBackgroundColor(resources.getColor(R.color.secondaryLightColor))


            }

        }
    }
    fun isValidText(value:String):Boolean{
        return !value.isNullOrBlank()
    }
    private fun initReclyclerView(){
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(this@ProductosActivity)
            val decoration = DividerItemDecoration(this@ProductosActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recycleView = RecyclerViewAdapterProductos(this@ProductosActivity)

            adapter = recycleView


        }
    }
    fun initViewModel(){
        viewModel = ViewModelProvider(this)[ProductosUsuarioViewModel::class.java]
        viewModel.deviceId = deviceId
        viewModel.compraId = compraId
        viewModel.loadProductos(this@ProductosActivity)
        viewModel.productosObservable.observe(this, Observer<List<ProductoDto>>{
            if(it==null){
                Toast.makeText(this@ProductosActivity,"Agregue productos a la lista", Toast.LENGTH_SHORT).show()
            }else{
                if(it.size==0)
                    Toast.makeText(this@ProductosActivity,"Agregue productos a la lista de compras",
                        Toast.LENGTH_SHORT).show()
                recycleView.productosUsuario = it.toMutableList()
                recycleView.notifyDataSetChanged()
            }
        })
    }

    override fun onChecked(productoId: Long) {
           val producto =  recycleView.productosUsuario.filter { it.productoId==productoId }.first()
                val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
                UserRepository::class.java).UpdateProducto(deviceId,compraId,productoId,producto)
            call.enqueue(object: Callback<MensajeOkResponseDto> {
                override fun onResponse(call: Call<MensajeOkResponseDto>, response: Response<MensajeOkResponseDto>) {
                    //if(response.isSuccessful){
                      //initViewModel()
                    //}
                }

                override fun onFailure(call: Call<MensajeOkResponseDto>, t: Throwable) {
                    Toast.makeText(this@ProductosActivity,"Ocurrio un error  "+ t.stackTrace, Toast.LENGTH_LONG).show()
                }

            })


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.layout_menu_products,menu)
        return super.onCreateOptionsMenu(menu)
    }
    fun  deleteProducts(){
        viewModel.deleteProducts(this,deviceId,compraId)
        recycleView.notifyDataSetChanged()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemDelete -> {
               if(showInterstitialValid()){
                   showInterstitial()
                   return false
               }
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Borrar productos?")
                alertDialogBuilder.setTitle("Quitar productos?")
                alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    deleteProducts()
                })
                alertDialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.show()

                return true
            }
            //  Otherwise, do nothing and use the core event handling

            // when clauses require that all possible paths be accounted for explicitly,
            //  for instance both the true and false cases if the value is a Boolean,
            //  or an else to catch all unhandled cases.
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onLongClick(productoId: Long) {
        val producto =  recycleView.productosUsuario.filter { it.productoId==productoId }.first()
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            UserRepository::class.java).DeleteProducto(deviceId,compraId,productoId)
        call.enqueue(object: Callback<MensajeOkResponseDto> {
            override fun onResponse(call: Call<MensajeOkResponseDto>, response: Response<MensajeOkResponseDto>) {
                ///if(response.isSuccessful){
                   val result =  recycleView.productosUsuario.remove(producto)
                    binding.rvProductos.adapter!!.notifyDataSetChanged()

               // }
            }

            override fun onFailure(call: Call<MensajeOkResponseDto>, t: Throwable) {
                Toast.makeText(this@ProductosActivity,"Ocurrio un error  "+ t.stackTrace, Toast.LENGTH_LONG).show()
            }

        })
    }

}