package com.cumulations.libreV2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.activity.CTDeviceBrowserActivity
import com.cumulations.libreV2.model.DataItem
import com.libreAlexa.R
import com.libreAlexa.databinding.CtRemotecommandItemBinding
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

        fun bindDataItem(dataItem: DataItem?, position: Int) {
            itemBinding.itemTitle.text = dataItem?.itemName
            itemBinding.itemTitle.isSelected = true

            val folder = " Folder"
            val file = " File "

            if (dataItem?.itemType == folder) {
                itemBinding.itemIcon.setImageResource(R.drawable.album_borderless)
            } else {
                if (!dataItem?.itemAlbumURL.isNullOrEmpty()) {
                    // default image for tidal file is tidal logo, else load the image in the URL
                    if (dataItem?.itemType == folder) {
                        PicassoTrustCertificates.getInstance(context).load(dataItem.itemAlbumURL)
                            .placeholder(R.drawable.album_borderless)
                            .error(R.drawable.album_borderless)
                            .into(itemBinding.itemIcon)
                    } else {
                        PicassoTrustCertificates.getInstance(context).load(dataItem?.itemAlbumURL)
                            .placeholder(R.drawable.songs_borderless)
                            .error(R.drawable.songs_borderless)
                            .into(itemBinding.itemIcon)
                    }
                } else {
                    itemBinding.itemIcon.setImageResource(R.drawable.songs_borderless)
                }
            }

            itemBinding.itemFavButton.visibility = View.VISIBLE

            if (dataItem?.favorite == null) return

            when (dataItem.favorite) {
                0 -> itemBinding.itemFavButton.visibility = View.GONE
                1 -> itemBinding.itemFavButton.setImageResource(R.mipmap.ic_remote_not_favorite)
                2 -> itemBinding.itemFavButton.setImageResource(R.mipmap.ic_remote_favorite)
            }

            itemBinding.rowLayout.setOnClickListener {
                if (context is CTDeviceBrowserActivity) {
                    context.onDataItemClicked(position)

                }
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



