package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.leanback.widget.*
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.view.ChannelListPresenter
import com.phoenix.phoenixplayer2.viewmodel.ListViewModel
import kotlinx.coroutines.*

class ChannelListFragment()
    : SingleLineVerticalFragment() {

    private lateinit var mChannelsAdapter: ArrayObjectAdapter
    private lateinit var mChannelsViewModel: ListViewModel
    private lateinit var mTvActivity: TvActivity
    lateinit var mSelectedChannel:Channel
    private val mUpdateHandler: Handler = Handler(Looper.getMainLooper())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTvActivity = activity as TvActivity
        mChannelsAdapter = ArrayObjectAdapter(ChannelListPresenter())
        onItemViewClickedListener = ChannelItemClickListener()
        setOnItemViewSelectedListener(ChannelItemSelectedListener())
        adapter =mChannelsAdapter

    }

    inner class ChannelItemSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is Channel){
                mSelectedChannel = item
            }
        }

    }

    inner class ChannelItemClickListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item != null){
                if (item is Channel){
                    onClickChannel(item)
                }
            }
        }
    }

    private fun onClickChannel(item: Channel){
        mTvActivity.switchChannel(item)
        mTvActivity.tune()
        mTvActivity.supportFragmentManager.popBackStack()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChannelList()
    }


    private fun setChannelList(){
        val tvActivity = activity as TvActivity
        mChannelsViewModel = tvActivity.listViewModel
        val repository = tvActivity.getRepository()
        mChannelsViewModel.data.observe(viewLifecycleOwner){
            val newList = repository.getGroup(it)
            if (mChannelsAdapter.size()>0){
                if ((mChannelsAdapter.get(0) as Channel).getGenreId() != it){
                    mChannelsAdapter.clear()
                }
            }
            if (newList != null){
                mChannelsAdapter.addAll(0, newList)
            }
        }
        /**
         * Due to the nature of TV apps,
         * RecyclerView is often controlled with a remote control.
         * (Especially, there are times when countless callbacks for selected are sent)
         * So I update it to the UI with a delay of 0.5 seconds
         * (otherwise there will be a huge rendering overload.
         * If there is another way to solve this, please advise me)
         * */

        mChannelsViewModel.forCategoryData.observe(viewLifecycleOwner){
            mUpdateHandler.removeCallbacksAndMessages(null)
            if (mChannelsAdapter.size() > 0){
                mChannelsAdapter.clear()
            }
            val newList = repository.getGroup(it)
            if (newList != null){
                mUpdateHandler.postDelayed({
                    mChannelsAdapter.addAll(0, newList)
                }, 550)
            }
        }
    }

}