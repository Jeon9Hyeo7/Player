package com.phoenix.phoenixplayer2.components

import android.content.Intent
import android.media.tv.TvContract
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.db.portal.PortalRepository
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Menu
import com.phoenix.phoenixplayer2.model.Portal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        if (intent.getStringExtra(Menu.TAG_PORTAL) == null){
            findHistory()
        }
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.portal_fragment, MainFragment())
                .commitNow()
        }*/
    }
    fun getRepository(): PortalRepository {
        return repository
    }

    private fun findHistory(){
        CoroutineScope(Dispatchers.IO).launch {
            val cursor = contentResolver.query(
                TvContract.Channels.CONTENT_URI,
                Channel.projection, null, null, null)

            if (cursor == null || cursor.count ==0){
                withContext(Dispatchers.Main){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.portal_fragment, MainFragment())
                        .commitNow()
                }
            }
            else if (repository.getConnectedPortal().isNotEmpty()){
                val connectedPortal = repository.getConnectedPortal()[0]
                val intent = Intent(this@MainActivity, TvActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(Portal.PORTAL_INTENT_TAG, connectedPortal)
                startActivity(intent)
            }
        }
    }



}