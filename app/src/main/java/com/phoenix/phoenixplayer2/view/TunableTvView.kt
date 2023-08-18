package com.phoenix.phoenixplayer2.view

import android.content.Context
import android.media.tv.TvView
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel

class TunableTvView(context: Context?, attrs: AttributeSet?)
    : TvView(context, attrs) {

        lateinit var viewModel:TvViewModel
        fun setModel(owner: LifecycleOwner, viewModel: TvViewModel){
            this.viewModel = viewModel
            viewModel.currentChannel.observe(owner) {
                tune(it.inputId!!, it.getUri())
            }
        }

}