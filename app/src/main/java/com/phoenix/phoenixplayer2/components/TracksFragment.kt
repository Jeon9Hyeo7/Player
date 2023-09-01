package com.phoenix.phoenixplayer2.components

import android.media.tv.TvTrackInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.Presenter
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.databinding.ItemAvBinding
import com.phoenix.phoenixplayer2.databinding.ItemChannelListBinding

class TracksFragment : SingleLineVerticalFragment(){

    private val mAdapter: ArrayObjectAdapter = ArrayObjectAdapter(TrackPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is TvTrackInfo){
                (activity as TvActivity).selectTrack(item)
            }
        }
        setOnItemViewSelectedListener { _, _, _, _ ->
        }
        adapter = mAdapter
        val viewModel = (activity as TvActivity).viewModel
        viewModel.selectedTracks.observe(this){
            mAdapter.clear()
            mAdapter.addAll(0, it)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lp = view.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = -120
        lp.width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        lp.height = resources.getDimensionPixelSize(R.dimen.dialog_height)
        view.layoutParams= lp

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
    }

    inner class TrackPresenter: Presenter(){

        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val inflater = LayoutInflater.from(parent?.context)
            val binding = ItemAvBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            if (item != null){
                if (item is TvTrackInfo && viewHolder is TrackViewHolder){
                    viewHolder.onBind(item)
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }
    inner class TrackViewHolder(val binding:ItemAvBinding): Presenter.ViewHolder(binding.root){

        fun onBind(tvTrackInfo: TvTrackInfo){
            TvTrackInfo.TYPE_AUDIO
            binding.track = tvTrackInfo
        }
    }
}