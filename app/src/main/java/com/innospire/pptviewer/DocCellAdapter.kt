package com.innospire.pptviewer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.innospire.pptviewer.R
import com.innospire.pptviewer.data.DocInfo
import kotlinx.android.synthetic.main.rv_doc_cell.mRvDocCell

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DocAdapter
 * Author: Victor
 * Date: 2023/10/26 10:56
 * Description: 
 * -----------------------------------------------------------------
 */

class DocCellAdapter(var context: Context,
                     var listener: AdapterView.OnItemClickListener?,
                     var parentPosition: Int)
    : RecyclerView.Adapter<DocCellViewHolder>() {

    var datas = ArrayList<DocInfo>()
    var isSetup: Boolean = true
    fun showDatas(docList: ArrayList<DocInfo>?) {
        datas.clear()
        docList?.let { datas.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocCellViewHolder {
        return DocCellViewHolder(inflate(R.layout.rv_doc_item_cell,parent),parentPosition)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: DocCellViewHolder, position: Int) {
        holder.mOnItemClickListener = listener
        holder.bindData(datas[position])
        holder.cardView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Perform actions when this view gains focus
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.teal_700))
                Log.d("RecyclerView", "CardView at position $position gained focus")
            } else {
                // Perform actions when the view loses focus
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.listItemColorPPT))
            }
        }
        if(isSetup){
            isSetup = false
            holder.cardView.requestFocus()
        }
    }

    fun inflate(layoutId: Int,parent: ViewGroup): View {
        var inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutId,parent, false)
    }
}