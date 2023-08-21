package com.phoenix.phoenixplayer2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Portal

class ListViewModel() : ViewModel(){

    companion object {
        private const val TAG: String = "PortalViewModel"
    }

    private val mGenreId = MutableLiveData<String>()
    private val mGenreIdForCategory = MutableLiveData<String>()
    val data : LiveData<String>
        get() {
            return mGenreId
        }
    val forCategoryData : LiveData<String>
        get() {
            return mGenreIdForCategory
        }

    init {
        mGenreId.value = "*"
    }

    fun set(genreId: String){
        mGenreId.value = genreId
    }

    fun selectCategory(genreId: String){
        mGenreIdForCategory.value = genreId
    }
}