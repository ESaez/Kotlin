package com.edison.adapter

import android.content.Context
import android.content.Intent
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.edison.R
import com.edison.activity.WebviewActivity
import com.edison.model.ModelCard
import java.util.ArrayList

class SwipeAdapter(context : Context, private val dataModelList : ArrayList<ModelCard>) :
        RecyclerView.Adapter<SwipeAdapter.MyViewHolder>(){

    private val inflater : LayoutInflater

    var onItemClick : ((ModelCard) -> Unit)? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = inflater.inflate(R.layout.card_view_layout, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tview_title_.setText(dataModelList[position].title)
        holder.tview_author_.setText(dataModelList[position].author)
        holder.tview_createdat_.setText(dataModelList[position].createdAt)

    }

    fun removeItem(position: Int){
        dataModelList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataModelList.size)
    }

    fun restoreItem(model : ModelCard, position: Int){
        dataModelList.add(position, model)
        notifyItemInserted(position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tview_title_: TextView
        var tview_author_: TextView
        var tview_createdat_: TextView


        init {

            tview_title_ = itemView.findViewById(R.id.txt_title) as TextView
            tview_author_ = itemView.findViewById(R.id.txt_author) as TextView
            tview_createdat_ = itemView.findViewById(R.id.txt_created_at) as TextView

            itemView.setOnClickListener{

                onItemClick?.invoke(dataModelList[adapterPosition])

            }
        }

    }

    override fun getItemCount(): Int {
        return dataModelList.size
    }

}