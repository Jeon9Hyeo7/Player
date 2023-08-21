package com.phoenix.phoenixplayer2.view

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.databinding.ItemChannelListBinding
import com.phoenix.phoenixplayer2.model.Channel

class ChannelListViewHolder(
    private val binding:ItemChannelListBinding)
    :Presenter.ViewHolder(binding.root) {
    private val context:Context = binding.root.context

    fun onBind(item: Any?){
            val drawable = ContextCompat.getDrawable(context, R.drawable.tv_default)
            val wrappedDrawable = DrawableCompat.wrap(
                drawable!!
            )
            DrawableCompat.setTint(
                wrappedDrawable,
                ContextCompat.getColor(context, R.color.color_theme_phoenix)
            )
            if (item is Channel){
                binding.channelDisplayName.text = item.displayName
                binding.channelDisplayNumber.text = item.displayNumber
                Glide.with(binding.root.context)
                    .load(item.getLogo())
                    .error(wrappedDrawable)
                    .placeholder(wrappedDrawable)
                    .centerCrop()
                    .into(binding.channelLogoInList)
            }
        }
}