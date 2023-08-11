package com.phoenix.phoenixplayer2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.phoenix.phoenixplayer2.db.PortalRepository
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.enums.EPGMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {
    companion object {
        val TAG : String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repo: PortalRepository = PortalRepository(this)
        val portal = Portal(title = "Example Title", url = "http://example.com", connected = true)
        CoroutineScope(Dispatchers.IO).launch {
            repo.insert(portal)
        }
        repo.getPortals().observe(this){
        }


/*
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commitNow()
        }
*/
    }
}