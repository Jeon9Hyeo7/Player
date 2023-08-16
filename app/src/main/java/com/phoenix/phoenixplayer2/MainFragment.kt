package com.phoenix.phoenixplayer2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import com.phoenix.phoenixplayer2.db.PortalRepository
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.view.PortalCardPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
  */
class MainFragment : RowsSupportFragment() {

    private val mRowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter(0, false))
    private lateinit var mListRowsAdapter: ArrayObjectAdapter
    companion object {
        private const val TAG: String = "MainFragment"
    }
    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListRowsAdapter = ArrayObjectAdapter(PortalCardPresenter())
        val headerItem = HeaderItem(resources.getString(R.string.portals))
        val listRow = ListRow(headerItem, mListRowsAdapter)
        mRowsAdapter.add(listRow)
        adapter = mRowsAdapter
        onItemViewSelectedListener = ItemSelectedListener()
        onItemViewClickedListener = ItemClickedListener()
        val repo = (activity as MainActivity).getRepository()
        repo.getPortals().observe(this) {
            mListRowsAdapter.clear()
            mListRowsAdapter.add("")
            mListRowsAdapter.addAll(1, it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootLayout: FrameLayout = view.parent as FrameLayout
        val layoutParams: FrameLayout.LayoutParams = rootLayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.main_page_top_margin)
        rootLayout.layoutParams = layoutParams
    }


    internal class ItemSelectedListener : OnItemViewSelectedListener{
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {

        }
    }
    internal class ItemClickedListener : OnItemViewClickedListener{
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {

        }

    }
}

