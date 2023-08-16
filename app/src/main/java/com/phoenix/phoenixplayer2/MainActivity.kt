package com.phoenix.phoenixplayer2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.phoenix.phoenixplayer2.db.PortalRepository
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.enums.EPGMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    companion object {
        private const val TAG : String = "MainActivity"
    }
    private lateinit var repository: PortalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repository = PortalRepository(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.portal_fragment, MainFragment())
                .commitNow()
        }
    }
    fun getRepository(): PortalRepository{
        return repository
    }

}