package com.phoenix.phoenixplayer2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.view.PortalCardPresenter
import com.phoenix.phoenixplayer2.view.PortalViewModel

/**
 *
  */
class MainFragment : RowsSupportFragment() , FragmentManager.OnBackStackChangedListener{

    private val mRowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter(0, false))
    private lateinit var mListRowsAdapter: ArrayObjectAdapter
    private lateinit var portalViewModel: PortalViewModel


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
            portalViewModel.set(it)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    private fun setupViewModel(){
        portalViewModel = ViewModelProvider(this)[PortalViewModel::class.java]
        portalViewModel.data.observe(viewLifecycleOwner) {
            if (mListRowsAdapter.size() == 0){
                mListRowsAdapter.add("")
            }
            mListRowsAdapter.removeItems(1, mListRowsAdapter.size()-1)
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




    private class ItemSelectedListener : OnItemViewSelectedListener{
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {


        }
    }


    inner class ItemClickedListener() : OnItemViewClickedListener{

        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            val fragmentManager = activity!!.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            if (item is String){
                transaction.add(R.id.main_frame, PortalEditFragment()).addToBackStack(null).hide(this@MainFragment).commit()
            }
            else if (item is Portal){
                transaction.add(R.id.main_frame, PortalActionFragment(item)).addToBackStack(null).hide(this@MainFragment).commit()
            }
        }

    }

    override fun onBackStackChanged() {
    }


}

