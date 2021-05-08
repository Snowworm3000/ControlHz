package com.example.controlhz

import android.R.attr.data
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter internal constructor(context: Context?, data: MutableList<Map<String, Any>>, configureApps: MutableMap<String, Int>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val mData: MutableList<Map<String, Any>>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    private var mConfigureApps: MutableMap<String, Int>

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.recyclerview_row, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = mData[position]["name"].toString()
        holder.myTextView.text = animal

        var pack:String = mData[position]["package"].toString()
        if(mConfigureApps.containsKey(pack)){
            holder.mode.text = listOf<String>("90Hz", "60Hz", "auto")[mConfigureApps[pack]!!]
        }else{
            holder.mode.text = "auto"
        }

        holder.appIcon.setImageDrawable(mData[position]["image"] as Drawable?)
//        holder.appIcon.
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var myTextView: TextView
        var appIcon:AppCompatImageView
        var mode:TextView
        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            appIcon = itemView.findViewById(R.id.appIcon)

            mode = itemView.findViewById(R.id.mode)
            myTextView = itemView.findViewById(R.id.tvAnimalName)
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String {
        return mData[id]["package"].toString() //Get name of app
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun addItem(index:Int, application:Map<String, Any>){
        mData.add(index, application)
//        Log.d(null, "add item $index $application")
//        this.notifyItemInserted(index)
        this.notifyDataSetChanged()
    }

    fun updateItem(index:Int, pack:String, value:Int){
        mConfigureApps[pack] = value
        this.notifyItemChanged(index)
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        mConfigureApps = configureApps
    }
}