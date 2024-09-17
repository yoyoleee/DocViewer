package com.innospire.pptviewer

import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innospire.pptviewer.data.DocGroupInfo
import kotlinx.android.synthetic.main.rv_doc_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DocViewHolder
 * Author: Victor
 * Date: 2023/10/26 10:57
 * Description: 
 * -----------------------------------------------------------------
 */

class DocViewHolder : RecyclerView.ViewHolder,OnClickListener {
    var mOnItemClickListener: OnItemClickListener? = null
    constructor(itemView: View) : super(itemView) {
        itemView.setOnClickListener(this)
    }

    fun bindData(data: DocGroupInfo?) {
        //itemView.mTvTypeName.text = data?.typeName

//        itemView.mRvDocCell.onFlingListener = null
//        LinearSnapHelper().attachToRecyclerView(itemView.mRvDocCell)

        val cellAdapter = DocCellAdapter(itemView.context,mOnItemClickListener,
            adapterPosition)
        cellAdapter.showDatas(data?.docList)

        itemView.mRvDocCell.adapter = cellAdapter
        val gridLayoutManager = GridLayoutManager(itemView.context, 3) // 3 columns
        itemView.mRvDocCell.layoutManager = gridLayoutManager
    }

    override fun onClick(v: View?) {
        mOnItemClickListener?.onItemClick(null,v,adapterPosition,0)
    }

}