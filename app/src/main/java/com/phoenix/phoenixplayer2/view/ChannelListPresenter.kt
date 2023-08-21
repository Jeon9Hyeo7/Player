package com.phoenix.phoenixplayer2.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.phoenix.phoenixplayer2.databinding.ItemChannelListBinding
import com.phoenix.phoenixplayer2.databinding.ItemPortalBinding

class ChannelListPresenter: Presenter() {



    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = ItemChannelListBinding.inflate(inflater, parent, false)
        return ChannelListViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val holder : ChannelListViewHolder = viewHolder as ChannelListViewHolder
        holder.onBind(item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}