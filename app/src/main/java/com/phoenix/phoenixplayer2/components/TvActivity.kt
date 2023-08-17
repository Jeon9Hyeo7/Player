package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.FragmentActivity
import com.phoenix.phoenixplayer2.model.Portal

class TvActivity :FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val connectedPortal = intent.getSerializableExtra(Portal.PORTAL_INTENT_TAG) as Portal

    }
}