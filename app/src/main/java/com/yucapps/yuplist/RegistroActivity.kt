package com.yucapps.yuplist

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.yucapps.yuplist.databinding.ActivityRegistroBinding
import com.yucapps.yuplist.model.Genero
import com.yucapps.yuplist.repository.CountryRepository
import com.yucapps.yuplist.repository.UserRepository
import com.yucapps.yuplist.request.UsuarioRegistroRequestDto
import com.yucapps.yuplist.response.CountryResponseDto
import com.yucapps.yuplist.utils.ApiService
import com.yucapps.yuplist.utils.RetrofitUtils
import com.yucapps.yuplist.viewmodel.RegistroUsuarioViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Logger

class RegistroActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegistroBinding
    lateinit var viewModel : RegistroUsuarioViewModel
    lateinit var deviceId: String
    lateinit var selectedCountry : CountryResponseDto
    lateinit var generoSelected : Genero




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        hideOrShowLayoutAgrupador(true)
        hideOrShowLayoutAgrupadorProgressBar(false)

        setupHyperlink()
        initViewModel()
        initAdMob()
        loadDeviceId()
        loadUser()
        loadCountries()
        loadSpinnerGenero()
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegistrar.setOnClickListener {
            sendData()
        }

    }
    private fun initAdMob(){
        val adRequest = AdRequest.Builder().build()
        binding.adViewRegistro.loadAd(adRequest)
    }
    private fun hideOrShowLayoutAgrupador(hide:Boolean){
        if(hide)
            binding.layoutAgrupadorRegistro.visibility = View.GONE
        else
            binding.layoutAgrupadorRegistro.visibility = View.VISIBLE

    }
    private fun hideOrShowLayoutAgrupadorProgressBar(hide:Boolean){
        if(hide)
            binding.layoutProgressBar.visibility = View.GONE
        else
            binding.layoutProgressBar.visibility = View.VISIBLE

    }


    fun loadSpinnerGenero(){
     //   val generoOptionsList = resources.getStringArray(R.array.tes)
        val generoOptionsList = listOf<String>("SELECCIONA UNO","HOMBRE","MUJER","PREFIERO NO DECIR")
        val generoList = mutableListOf<Genero>()
        val id : Short = -1
        for (gen in generoOptionsList){
            generoList.add(Genero(id,gen))
            id.inc()
        }
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1,generoList)
        binding.spnGenero.adapter = adapter

        binding.spnGenero.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                generoSelected = parent!!.selectedItem as Genero
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }
    fun loadSpinner(){
        binding.progressBar.progress  = 80
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1,viewModel.countries)
        binding.spnCountries.adapter= adapter
        binding.spnCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCountry = parent!!.getItemAtPosition(position) as CountryResponseDto
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
    fun loadCountries(){
        viewModel.triedCountries = true
        var result:ArrayList<CountryResponseDto> = ArrayList()
        result.add(CountryResponseDto(-1,"NO SE ENCUENTRA MI PAÍS"))

        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            CountryRepository::class.java).GetAll("/paises")
        call.enqueue(object: Callback<List<CountryResponseDto>> {
            override fun onResponse(call: Call<List<CountryResponseDto>>, response: Response<List<CountryResponseDto>>) {
                viewModel.countries = response.body()!!
                notifyUpdateElements()
                loadSpinner()

            }

            override fun onFailure(call: Call<List<CountryResponseDto>>, t: Throwable) {
                Toast.makeText(applicationContext,"Países no disponibles. Por favor Activa tu wifi/datos",Toast.LENGTH_LONG).show()
                notifyUpdateElements()
            }

        })
    }
    fun loadUser(){
        viewModel.triedUser = true
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(UserRepository::class.java).Get(deviceId)
        call.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                viewModel.userRegistered  = response.isSuccessful
                notifyUpdateElements();
                if(viewModel.userRegistered){
                    goNextScreen()
                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@RegistroActivity,"Usuario no disponible. Por favor Activa tu wifi/datos",Toast.LENGTH_LONG).show()
                notifyUpdateElements()
            }

        })
    }

    private fun notifyUpdateElements() {
        if(viewModel.userRegistered!=null && viewModel.countries!=null){
            hideOrShowLayoutAgrupadorProgressBar(true)
            hideOrShowLayoutAgrupador(false)
            binding.btnRegistrar.isEnabled = true
        }else{
            if(viewModel.triedCountries && viewModel.triedUser){
                hideOrShowLayoutAgrupadorProgressBar(true)
                hideOrShowLayoutAgrupador(false)
            }

        }
    }

    fun validateDataBeforeSend():Boolean{
        var mail =binding.edtEmail.text.toString()
        if(mail.isNullOrBlank()){
            binding.inputLayoutMail.error = "Correo es requerido"
            binding.inputLayoutMail.requestFocus()
            return false
        }else
            binding.inputLayoutMail.error = null
        val personName = binding.edtNombreCompleto.text.toString()
        if(personName.isNullOrBlank()){
            binding.inputLayoutNombre.error = "Nombre completo es requerido"
            binding.inputLayoutNombre.requestFocus()
            return false
        }else{
            binding.inputLayoutNombre.error = null
        }
        var edad = binding.edtEdad.text.toString().toLongOrNull()
        if(edad==null){
            binding.inputLayoutEdad.error = "Edad es requerido"
            binding.inputLayoutEdad.requestFocus()
            return false
        }else if(edad<=0){
            binding.inputLayoutEdad.error = "Edad no es válida"
            binding.inputLayoutEdad.requestFocus()
            return false
        }else{
            binding.inputLayoutEdad.error=null
        }
        if(binding.chkTerminosCondiciones.isChecked==false){
            Toast.makeText(RegistroActivity@this,"Por favor acepte términos y condiciones",Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    fun sendData(){
        if(validateDataBeforeSend()){
            var request = createDataRequest()
            registerUser(request)
        }

    }
    fun goNextScreen(){
        val intent = Intent(this,ComprasActivity::class.java).apply {
            putExtra("dispositivoId",deviceId)
        }

        startActivity(intent)
    }
    fun registerUser(request:UsuarioRegistroRequestDto){
        val call =    RetrofitUtils.getRetrofit(ApiService.getUrl(applicationContext)).create(
            UserRepository::class.java).Register(request)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    Toast.makeText(this@RegistroActivity,"Registrado!!!",Toast.LENGTH_SHORT).show()
                    goNextScreen()
                }else{
                    Toast.makeText(applicationContext,"No fue posible registrar",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(applicationContext,"Activa tu wifi/datos-No fue posible registrar",Toast.LENGTH_LONG).show()

            }

        })
    }
    fun createDataRequest(): UsuarioRegistroRequestDto {
        val personName = binding.edtNombreCompleto.text.toString()
        var mail =binding.edtEmail.text.toString()
        var country = selectedCountry
        var generoId  = generoSelected.id
        var edad = binding.edtEdad.text.toString().toShort()
        return UsuarioRegistroRequestDto(mail,"123456789",edad,country.paisId,generoId,personName,deviceId)
    }
    private fun loadDeviceId(){
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[RegistroUsuarioViewModel::class.java]
    }
    private fun setupHyperlink() {
         binding.txvTerminosCondiciones.movementMethod= LinkMovementMethod.getInstance();
        binding.txvTerminosCondiciones.setLinkTextColor(Color.RED)
    }
}