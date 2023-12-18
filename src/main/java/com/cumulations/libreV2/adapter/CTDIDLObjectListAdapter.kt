package com.cumulations.libreV2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cumulations.libreV2.activity.CTDMSBrowserActivityV2
import com.cumulations.libreV2.activity.CTUpnpFileBrowserActivity
import com.libreAlexa.R
import com.libreAlexa.databinding.CtListItemDidlObjectBinding
import com.libreAlexa.util.LibreLogger
import com.squareup.picasso.Picasso
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item
import java.net.MalformedURLException

class CTDIDLObjectListAdapter(
    val context: Context,
    var didlObjectList: MutableList<DIDLObject>?
) : RecyclerView.Adapter<CTDIDLObjectListAdapter.DIDLObjectItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): DIDLObjectItemViewHolder {
        val itemBinding = CtListItemDidlObjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DIDLObjectItemViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: DIDLObjectItemViewHolder, position: Int) {
        val scanResultItem = didlObjectList?.get(position)
        viewHolder.bindDIDLObjectItem(scanResultItem, position)
    }

    override fun getItemCount(): Int {
        return didlObjectList?.size!!
    }

    inner class DIDLObjectItemViewHolder(val itemBinding: CtListItemDidlObjectBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindDIDLObjectItem(didlObject: DIDLObject?, position: Int) {
            itemBinding.tvDidlItemName.text = didlObject?.title
            itemBinding.tvDidlItemArtist.text = didlObject?.creator
            if (didlObject is Item) {
                LibreLogger.d("bindDataItem", "Object is of type Item : ")

                itemBinding.tvDidlItemArtist.visibility = View.VISIBLE
                itemBinding.tvContainerCount.visibility = View.GONE
                val uri =
                    didlObject.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI::class.java)
                if (uri != null) {
                    try {
                        val androiduri = android.net.Uri.parse(uri.toString())
                        Picasso.with(context)
                            .load(androiduri)
                            .placeholder(R.drawable.riva_songs_border)
                            .error(R.drawable.riva_songs_border)
                            .into(itemBinding.ivDidlItemAlbumArt)
                        LibreLogger.d(
                            "bindDataItem",
                            "Item : " + uri.toURL() + " " + didlObject.getTitle()
                        )
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                        LibreLogger.d("bindDataItem", "exception " + e.message)
                    }

                } else {
                    itemBinding.ivDidlItemAlbumArt.setImageResource(R.drawable.riva_songs_border)
                }
            } else {
                when {
                    didlObject?.clazz?.value?.contains("album", true)!! ->
                        itemBinding.ivDidlItemAlbumArt.setImageResource(R.drawable.albums_border)

                    didlObject.clazz?.value?.contains("artist", true)!! ->
                        itemBinding.ivDidlItemAlbumArt.setImageResource(R.drawable.artists_border)

                    didlObject.clazz?.value?.contains("genre", true)!! ->
                        itemBinding.ivDidlItemAlbumArt.setImageResource(R.drawable.album_borderless)
                    else -> itemBinding.ivDidlItemAlbumArt.setImageResource(R.drawable.riva_songs_border)
                }

                itemBinding.tvContainerCount.visibility = View.VISIBLE
                itemBinding.tvDidlItemArtist.visibility = View.GONE
                itemBinding.tvContainerCount.text = (didlObject as Container).childCount?.toString()
            }

            itemBinding.llDidlItem.setOnClickListener {
                if (context is CTUpnpFileBrowserActivity) {
                    context.handleDIDLObjectClick(position)
                }

                if (context is CTDMSBrowserActivityV2) {
                    context.handleDIDLObjectClick(position)
                }
            }
        }
    }

    fun updateList(didlObjectList: MutableList<DIDLObject>?) {
        this.didlObjectList = didlObjectList
        notifyDataSetChanged()
    }
}



