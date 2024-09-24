package com.innospire.pptviewer

import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.innospire.pptviewer.data.DocInfo
import com.cherry.lib.doc.bean.FileType
import com.cherry.lib.doc.util.FileUtils
import kotlinx.android.synthetic.main.rv_doc_item_cell.view.*
import java.io.File

class DocCellViewHolder : RecyclerView.ViewHolder,OnClickListener {
    val cardView: CardView = itemView.findViewById(R.id.mCvDocCell)
    var mOnItemClickListener: OnItemClickListener? = null
    var parentPosition: Int = 0
    constructor(itemView: View, groupPosition: Int) : super(itemView) {
        parentPosition = groupPosition
        itemView.setOnClickListener(this)
    }

    fun bindData(data: DocInfo?) {
        var typeIcon = data?.getTypeIcon() ?: -1
        var file = File(data?.path)
        if (typeIcon == -1) {
            if (file.exists()) {
                itemView.mIvType.load(File(data?.path))
            } else {
                itemView.mIvType.load(com.cherry.lib.doc.R.drawable.all_doc_ic)
            }
        } else {
            val JPGFile = File(file.parent, file.nameWithoutExtension + ".jpg")
            if(JPGFile.exists()){
                itemView.mIvType.load(JPGFile)
            }else{
                itemView.mIvType.load(typeIcon)
            }
        }
        itemView.mTvFileName.text = data?.fileName
        itemView.mTvFileDes.text = "${data?.lastModified}"

        val type = FileUtils.getFileTypeForUrl(data?.path)
        if (type == FileType.PPT || type == FileType.PPTX) {
            itemView.mCvDocCell.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    itemView.mCvDocCell.resources,
                    R.color.listItemColorPPT,
                    itemView.mCvDocCell.context.theme
                )
            )
        }
    }

    override fun onClick(v: View?) {
        mOnItemClickListener?.onItemClick(null,v,adapterPosition,parentPosition.toLong())
    }

}
