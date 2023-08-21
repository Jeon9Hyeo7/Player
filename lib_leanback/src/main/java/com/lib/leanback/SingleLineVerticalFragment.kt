package com.lib.leanback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.R
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView

open class SingleLineVerticalFragment: VerticalGridSupportFragment() {

    private lateinit var verticalGridView: VerticalGridView

    companion object{
        const val CHANNEL_LIST_BACKSTACK = "channel_list_in_backstack"
        const val CATEGORY_BACKSTACK = "category_in_backstack"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalGridView = view.findViewById(R.id.browse_grid)!!

    }

    private fun setupFragment() {
        val verticalGridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_NONE,
            false
        )
        verticalGridPresenter.shadowEnabled = false
        verticalGridPresenter.numberOfColumns = 1
        gridPresenter = verticalGridPresenter
    }


    private fun setSelectedPositionSmooth(index: Int) {
        verticalGridView.selectedPosition = index
    }

    open fun show(i: Int, tag: String? = null) {
        val sfm = activity?.supportFragmentManager!!
        sfm.beginTransaction().show(this).addToBackStack(tag).commit()
        if (view != null) {
            view?.requestFocus()
            if (i<adapter.size()-1 && i>=0){
                setSelectedPositionSmooth(i)
            }
        }
    }


}