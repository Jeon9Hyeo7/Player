package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.Presenter
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.databinding.ItemResultBinding

class SettingsResultFragment : SingleLineVerticalFragment(){

    lateinit var mAdapter: ArrayObjectAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ArrayObjectAdapter(ResultPresenter())
        adapter = mAdapter
    }

    fun setResult(results: List<Pair<String, String>>){
        mAdapter.clear()
        mAdapter.addAll(0, results)
    }

    fun clear(){
        mAdapter.clear()

    }


    class ResultPresenter: Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val inflater = LayoutInflater.from(parent?.context)
            val binding = ItemResultBinding.inflate(inflater, parent, false)
            return ResultViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val resultViewHolder = viewHolder as ResultViewHolder
            if (item is Pair<*, *>){
                @Suppress("UNCHECKED_CAST")
                resultViewHolder.onBind(item as Pair<String, String>)
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

        }

        inner class ResultViewHolder(val binding:ItemResultBinding) : ViewHolder(binding.root){

            fun onBind(result: Pair<String, String>){
                binding.title = result.first
                binding.result = result.second
            }

        }

    }
}