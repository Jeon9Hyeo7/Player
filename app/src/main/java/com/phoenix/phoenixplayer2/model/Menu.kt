package com.phoenix.phoenixplayer2.model

import android.graphics.drawable.Drawable

data class Menu(val text: String?, val icon:Drawable){
    
    companion object{
        const val TAG_PORTAL = "portal"
    }
}
