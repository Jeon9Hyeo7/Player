package com.phoenix.phoenixplayer2.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.phoenix.phoenixplayer2.databinding.ItemCategoryBinding
import com.phoenix.phoenixplayer2.databinding.ItemChannelListBinding
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.VodCategory

class CategoryPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val holder : CategoryViewHolder = viewHolder as CategoryViewHolder
        holder.onBind(item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }


    inner class CategoryViewHolder(private val binding:ItemCategoryBinding)
        : Presenter.ViewHolder(binding.root){
        fun onBind(item: Any?){
            when (item) {
                is Category -> {
                    binding.categoryText.text = item.title
                }
                is VodCategory -> {
                    binding.categoryText.text = item.categoryStr
                }
                is String -> {
                    binding.categoryText.text = item
                }
            }
        }
    }


}
