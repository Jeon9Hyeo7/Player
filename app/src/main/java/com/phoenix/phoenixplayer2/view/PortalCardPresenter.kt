package com.phoenix.phoenixplayer2.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.phoenix.phoenixplayer2.databinding.ItemPortalBinding

class PortalCardPresenter : PresenterSelector(){
    override fun getPresenter(item: Any?): Presenter {
        return PortalMainPresenter()
    }

    internal class PortalMainPresenter : Presenter(){

        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val inflater = LayoutInflater.from(parent?.context)
            val binding = ItemPortalBinding.inflate(inflater, parent, false)
            return PortalViewHolder(binding)

        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val holder = viewHolder as PortalViewHolder
            holder.onBind(item)

        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }
}