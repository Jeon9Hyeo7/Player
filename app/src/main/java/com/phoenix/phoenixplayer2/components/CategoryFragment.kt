package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.leanback.widget.ArrayObjectAdapter
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.view.CategoryPresenter
import com.phoenix.phoenixplayer2.viewmodel.ListViewModel

class CategoryFragment: SingleLineVerticalFragment() {
    private lateinit var mTvActivity: TvActivity
    private val mCategoryAdapter: ArrayObjectAdapter
            = ArrayObjectAdapter(CategoryPresenter())

    private lateinit var mListViewModel: ListViewModel
    private var mSelectedCategory: Any? = null


    companion object{
        private const val TAG = "CategoryFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTvActivity = activity as TvActivity
        mListViewModel = mTvActivity.listViewModel
        adapter = mCategoryAdapter
        setEventListener()
    }

    fun setCategories(categories: List<Category>){
        mCategoryAdapter.add(Category.FAVORITE)
        mCategoryAdapter.addAll(1, categories)
    }

    private fun setEventListener() {
        setOnItemViewClickedListener { _, _, _, _ ->
            mTvActivity.popCategory()
        }
        setOnItemViewSelectedListener { _, item, _, _ ->
            mSelectedCategory = if (item is Category){
                mListViewModel.selectCategory(item.id!!)
                item
            } else{
                mListViewModel.selectCategory(Category.FAVORITE)
                Category.FAVORITE
            }
        }
    }

    fun show() {
        val selectedChannel = mTvActivity.getChannelListInstance()
        var index = -1
        if (mSelectedCategory == Category.FAVORITE){
            index = 0
        }
        else{
            for (i in 0 until mCategoryAdapter.size()) {
                val category = mCategoryAdapter[i]
                if (category is Category){
                    if (category.id == selectedChannel.getGenreId()){
                        index = i
                    }
                }
            }
        }
        super.show(index, CATEGORY_BACKSTACK)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (view != null){
            val parentLayout = view?.parent?.parent as ConstraintLayout
            val categoryId = R.id.category_container
            val constraintSet = ConstraintSet()
            if (hidden){
                constraintSet.clone(parentLayout)
                constraintSet.clear(categoryId, ConstraintSet.START)
                constraintSet.clear(categoryId, ConstraintSet.END)
                constraintSet.connect(
                    categoryId,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                constraintSet.applyTo(parentLayout)
            }
            else{
                constraintSet.clone(parentLayout)
                constraintSet.clear(categoryId, ConstraintSet.START)
                constraintSet.clear(categoryId, ConstraintSet.END)
                constraintSet.connect(
                    categoryId,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                TransitionManager.beginDelayedTransition(parentLayout)
                constraintSet.applyTo(parentLayout)
            }
        }


    }
}