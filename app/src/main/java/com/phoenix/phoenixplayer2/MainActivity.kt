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
import org.jetbrains.annotations.TestOnly
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



    /*override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_PROG_GREEN){
            val portal = Portal(title = "Example Title", url = "http://example.com", connected = true, exp_date = "2023-08-16")
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(portal)
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_PROG_BLUE){
            CoroutineScope(Dispatchers.IO).launch {
                repository.clear()
            }
        }
        return super.onKeyUp(keyCode, event)
    }*/
}