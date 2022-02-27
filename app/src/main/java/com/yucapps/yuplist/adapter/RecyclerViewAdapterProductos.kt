package com.yucapps.yuplist.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yucapps.yuplist.ProductoCheckedListener
import com.yucapps.yuplist.R
import com.yucapps.yuplist.model.ProductoDto
import com.yucapps.yuplist.utils.KeyboardHelper
import kotlinx.coroutines.processNextEventInCurrentThread
import java.text.NumberFormat
import java.util.logging.Logger

class RecyclerViewAdapterProductos(val productoCheckedListener: ProductoCheckedListener):RecyclerView.Adapter<RecyclerViewAdapterProductos.MyViewHolder>() {

    public var productosUsuario = mutableListOf<ProductoDto>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapterProductos.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_row_productos,parent,false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapterProductos.MyViewHolder, position: Int) {
        val data = productosUsuario[position]
        holder.bind(data)
        holder.checkedItem.setOnClickListener {

            if(data.checked){
                holder.checkedItem.isChecked = false
                holder.txvNombreProducto.paintFlags =    Paint.ANTI_ALIAS_FLAG
                data.checked=false
                productoCheckedListener.onChecked(data.productoId)

            }else{
                holder.checkedItem.isChecked = true
                data.checked = true
                holder.txvNombreProducto.paintFlags =  Paint.STRIKE_THRU_TEXT_FLAG
                productoCheckedListener.onChecked(data.productoId)
            }
            notifyItemChanged(position)

        }
        holder.view.setOnLongClickListener{
            showDialog(holder.view.context,data.productoId)
            return@setOnLongClickListener true
        }


        holder.txvPrecioProducto.setOnEditorActionListener { v, actionId, event ->

            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val price = holder.txvPrecioProducto.text.toString().toFloatOrNull()
                    if(price!=null) {
                        data.precio = price
                        holder.txvPrecioProducto.setText(NumberFormat.getCurrencyInstance().format(data.precio!!))
                        holder.txvPrecioProducto.clearFocus()
                        KeyboardHelper.hide(holder.view)
                        productoCheckedListener.onChecked(data.productoId)
                        Snackbar.make(holder.view, "Precio guardado", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
                else -> false
            }
        }
    }
    fun showDialog(context: Context, productId: Long){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Borrar producto?")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            productoCheckedListener.onLongClick(productId)

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.setCancelable(false)
        builder.show()
    }
    override fun getItemCount(): Int {
       return productosUsuario.size
    }
    class MyViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val txvNombreProducto = view.findViewById<TextView>(R.id.txvProductName)
        val txvPrecioProducto = view.findViewById<EditText>(R.id.edtPrice)
        var checkedItem = view.findViewById<CheckBox>(R.id.chkProductDone)

        fun bind(data:ProductoDto){
            txvNombreProducto.text = data.nombre
            if(data.precio!=null){
                txvPrecioProducto.setText(NumberFormat.getCurrencyInstance().format(data.precio!!))
            }
            else{
                txvPrecioProducto.text = null
            }
            checkedItem.isChecked = data.checked
            if(data.checked){
                txvNombreProducto.paintFlags =  Paint.STRIKE_THRU_TEXT_FLAG
            }else{
                txvNombreProducto.paintFlags =    Paint.ANTI_ALIAS_FLAG
            }
        }

    }
}