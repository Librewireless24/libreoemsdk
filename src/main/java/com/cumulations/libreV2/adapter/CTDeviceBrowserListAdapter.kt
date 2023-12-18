package com.cumulations.libreV2.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.activity.CTDeviceBrowserActivity
import com.cumulations.libreV2.model.DataItem
import com.libreAlexa.R
import com.libreAlexa.databinding.CtRemotecommandItemBinding
import com.libreAlexa.util.LibreLogger
import com.libreAlexa.util.PicassoTrustCertificates

class CTDeviceBrowserListAdapter(
    val context: Context,
    var dataItemList: MutableList<DataItem>?
) : RecyclerView.Adapter<CTDeviceBrowserListAdapter.DataItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): DataItemViewHolder {
        val itemBinding =
            CtRemotecommandItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataItemViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: DataItemViewHolder, position: Int) {
        val scanResultItem = dataItemList?.get(position)
        viewHolder.bindDataItem(scanResultItem, position)
    }

    override fun getItemCount(): Int {
        return dataItemList?.size!!
    }

    inner class DataItemViewHolder(val itemBinding: CtRemotecommandItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindDataItem(dataItem: DataItem?, position: Int)
        {
            itemBinding.itemTitle.text = dataItem?.itemName
            itemBinding.itemTitle.isSelected = true

            val folder = "Folder"
            val file = "File"

//            if (dataItem?.itemType == folder) {
//                itemBinding.itemIcon.setImageResource(R.drawable.album_borderless)
//            }

            itemBinding.rowLayout.setOnClickListener {
                if (context is CTDeviceBrowserActivity) {
                    //context.onDataItemClicked(position)
                    context.selectItem(position)
                    LibreLogger.d("DeviceBrowserListAdapter","device browser ondataClicked\n")
                }
            }

            itemBinding.layItem.setOnClickListener {
                if (context is CTDeviceBrowserActivity) {
                    context.selectItem(position)
                    LibreLogger.d("DeviceBrowserListAdapter","device browser ondataClicked\n")
                }
            }

            LibreLogger.d("DeviceBrowser","DataViewHolder item type Before**\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)

            if (dataItem.itemType.equals("Folder")) {
                itemBinding.itemFolder.visibility = View.VISIBLE
                itemBinding.itemIcon.setImageResource(R.drawable.riva_album_art)
                LibreLogger.d("DeviceBrowser","DataViewHolder item type IF**\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)

            }

            else if (dataItem.itemType.equals("File"))
            {
                itemBinding.itemIcon.setImageResource(R.drawable.riva_songs_border)
                itemBinding.itemFolder.visibility = View.GONE
                LibreLogger.d("DeviceBrowser","DataViewHolder item type ELSE_IF*\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)

//                if (!dataItem?.itemAlbumURL.isNullOrEmpty()) {
//                    itemBinding.itemFolder?.visibility = View.GONE
//                    if (dataItem?.itemAlbumURL.equals("airable.jpg"))
//                    {
//                        itemBinding.itemIcon?.setImageResource(R.drawable.riva_songs_border)
//                    }
//                    else
//                    {
//                        PicassoTrustCertificates.getInstance(context).load(dataItem?.itemAlbumURL)
//                            .placeholder(R.mipmap.dms_album_art)
//                            .error(R.mipmap.dms_album_art)
//                            .into(itemBinding.itemIcon)
//                    }
//                }

            }
//            else if (dataItem?.itemName.equals("No content available"))
//            {
//                LibreLogger.d("DeviceBrowser","DataViewHolder item type noContentAvailable *\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)
//
//                itemBinding.itemIcon?.setImageResource(R.drawable.riva_songs_border)
//                itemBinding.itemFolder?.visibility = View.GONE
//            }

//            else {
//                LibreLogger.d("DeviceBrowser","DataViewHolder item type ELSE_two**\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)
//
//                if (!dataItem?.itemAlbumURL.isNullOrEmpty()) {
//                    // default image for tidal file is tidal logo, else load the image in the URL
//                    if (dataItem?.itemType == folder) {
//                        PicassoTrustCertificates.getInstance(context).load(dataItem.itemAlbumURL)
//                            .placeholder(R.drawable.album_borderless)
//                            .error(R.drawable.album_borderless)
//                            .into(itemBinding.itemIcon)
//                    } else {
//                        PicassoTrustCertificates.getInstance(context).load(dataItem?.itemAlbumURL)
//                            .placeholder(R.drawable.riva_songs_borderless)
//                            .error(R.drawable.riva_songs_borderless)
//                            .into(itemBinding.itemIcon)
//                    }
//                }
//                else {
//                    LibreLogger.d("DeviceBrowser","DataViewHolder item type ELSE_three**\n"+ dataItem!!.itemType+"itemName\n"+dataItem.itemName)
//
//                    itemBinding.itemIcon.setImageResource(R.drawable.riva_songs_borderless)
//                    itemBinding.itemFolder?.visibility = View.GONE
////                    itemView.item_icon?.setImageResource(R.drawable.riva_songs_border)
//                }
//            }

            itemBinding.itemFavButton.visibility = View.GONE

            if (dataItem.favorite == null) return

            when (dataItem.favorite) {
                0 -> itemBinding.itemFavButton.visibility = View.GONE
                1 -> itemBinding.itemFavButton.setImageResource(R.mipmap.ic_remote_not_favorite)
                2 -> itemBinding.itemFavButton.setImageResource(R.mipmap.ic_remote_favorite)
            }



            itemBinding.itemFavButton.setOnClickListener {
                if (context is CTDeviceBrowserActivity) {
                    context.onFavClicked(position)
                }
            }

        }
    }

    fun updateList(dataItemList: MutableList<DataItem>?) {
        this.dataItemList = dataItemList
        notifyDataSetChanged()
    }
}



