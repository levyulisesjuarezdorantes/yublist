package com.yucapps.yuplist.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yucapps.yuplist.ComprasListener
import com.yucapps.yuplist.ProductosActivity
import com.yucapps.yuplist.R
import com.yucapps.yuplist.model.CompraDto
import com.yucapps.yuplist.model.CompraHeaderDto

class RecyclerViewAdapterCompras(val comprasListener: ComprasListener):RecyclerView.Adapter<RecyclerViewAdapterCompras.MyViewHolder>() {

    lateinit var deviceId :String
    var comprasUsuario = mutableListOf<CompraHeaderDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapterCompras.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_row_compras,parent,false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapterCompras.MyViewHolder, position: Int) {
        var data = comprasUsuario[position]
        holder.bind(data)
        holder.layout.setOnClickListener{
            val context =  holder.view.context
            val intent = Intent(context, ProductosActivity::class.java)
            intent.putExtra("deviceId",deviceId)
            val compraId = data.compraId
            intent.putExtra("compraId",compraId)
            intent.putExtra("compraNameList",data.nombre)
            context.startActivity(intent)
        }
        holder.view.setOnLongClickListener{
           showDialog(holder.view.context,data.compraId)
            return@setOnLongClickListener true
        }
    }
    fun showDialog(context:Context,compraId: Long){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Borrar lista de compra")
      //  builder.setMessage("Se eliminarán todos los productos")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
               comprasListener.onLongClick(compraId)

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.setCancelable(false)
        builder.show()
    }
    override fun getItemCount(): Int {
       return comprasUsuario.size
    }
    class MyViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val txvNombreLista =   view.findViewById<TextView>(R.id.item_title)
        val layout = view.findViewById<LinearLayout>(R.id.container_list_compras)
        val txvProductosCount = view.findViewById<TextView>(R.id.products_count)


        fun bind(data:CompraHeaderDto) {
            txvNombreLista.text = data.nombre
            txvProductosCount.text = ""+data.productosCount + " producto(s). Total:"+data.totalCompra
        }


    }
}