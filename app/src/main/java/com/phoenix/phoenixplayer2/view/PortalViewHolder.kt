package com.phoenix.phoenixplayer2.view

import android.content.Context
import android.view.View.VISIBLE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.Presenter
import com.phoenix.phoenixplayer2.databinding.ItemPortalBinding
import com.phoenix.phoenixplayer2.model.Portal

class PortalViewHolder(private val binding: ItemPortalBinding)
    : Presenter.ViewHolder(binding.root) {

    init {

    }

    fun onBind(item: Any?){
        if (item is Portal) {
            binding.portalName.text = item.title
            binding.portalUrl.text = item.url
            binding.portalExpDate.text = item.exp_date
        }
        else {
            binding.addPortalsView.root.visibility = VISIBLE
        }
    }
}