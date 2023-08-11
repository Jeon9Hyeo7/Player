package com.gil.phoenixplayer2

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Loads [MainFragments].
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragments())
                .commitNow()
        }
    }
}