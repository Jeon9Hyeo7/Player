package com.phoenix.phoenixplayer2.components

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.leanback.preference.LeanbackSettingsFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceScreen
import com.phoenix.phoenixplayer2.BuildConfig
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.DeviceManager
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.Profile
import java.util.*

@Suppress("DEPRECATION")
class SettingsFragment: LeanbackSettingsFragment() {


    companion object{
        const val PREFERENCE_RESOURCE_ID = "preferenceResource"
        const val PREFERENCE_ROOT = "root"
    }


    override fun onPreferenceStartFragment(
        caller: PreferenceFragment?,
        pref: Preference?
    ): Boolean {
        return false
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragment?,
        pref: PreferenceScreen?
    ): Boolean {
        val frag = buildPreferenceFragment(R.xml.prefs, pref?.key!!)
        startPreferenceFragment(frag)
        return true
    }

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment(R.xml.prefs, null))
    }


    private fun buildPreferenceFragment(preferenceResId: Int, root: String?): PreferenceFragment {
        val fragment: PreferenceFragment =
            PrefFragment()
        val args = Bundle()
        args.putInt(PREFERENCE_RESOURCE_ID, preferenceResId)
        args.putString(PREFERENCE_ROOT, root)
        fragment.arguments = args
        return fragment
    }


    @SuppressLint("ValidFragment")
    class PrefFragment : LeanbackPreferenceFragment() {
        var resultFragment: SettingsResultFragment? =null

        var portal:Portal? = null
        var profile: Profile? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            portal = activity.intent.getSerializableExtra(Portal?.PORTAL_INTENT_TAG) as Portal
            profile = activity.intent.getSerializableExtra(Profile.TAG_INTENT_PROFILE) as Profile
            resultFragment = (activity as FragmentActivity).supportFragmentManager
                .findFragmentById(R.id.settings_result_container)
                    as SettingsResultFragment

        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            val root = arguments.getString(PREFERENCE_ROOT, null)
            val prefResId = arguments.getInt(PREFERENCE_RESOURCE_ID)
            if (root == null) {
                addPreferencesFromResource(prefResId)
            } else {
                setPreferencesFromResource(prefResId, root)
            }
        }

        override fun onStop() {
            super.onStop()

            resultFragment?.clear()
        }

        @SuppressLint("HardwareIds")
        override fun onPreferenceTreeClick(preference: Preference?): Boolean {

            if (preference?.title == resources.getString(R.string.settings_portal_information)){
                val nameResult = Pair("Portal Name", portal?.title!!)
                val expResult = Pair("Expired Date", portal?.exp_date!!)
                val urlResult = Pair("Portal Url", portal?.url!!)
                val macResult = Pair("Registered MAC", "${portal?.macAddress}(${portal?.MAC_TYPE})")
                val parts = profile
                    ?.defaultLocale
                    ?.split("[_.]".toRegex())!!
                    .toTypedArray()
                val language = parts[0]
                val country = parts[1]
                val encoding = parts[2]
                val locale = Locale(language, country, encoding)
                val localeResult = Pair("Language", "${locale.displayLanguage} " +
                        "- ${locale.displayCountry}")
                val results = mutableListOf<Pair<String, String>>()
                results.add(nameResult)
                results.add(urlResult)
                results.add(expResult)
                results.add(macResult)
                results.add(localeResult)
                resultFragment?.setResult(results)
            }

            else if (preference?.title == resources.getString(R.string.settings_box_information)){
                val realMac = DeviceManager.getMacAddress()
                val serialNumber = DeviceManager.getSerialNumber(context = context)

                val model = Build.MODEL
                val version = BuildConfig.VERSION_NAME
                val results = mutableListOf<Pair<String, String>>()
                results.add(Pair("MAC Address", realMac))
                results.add(Pair("Serial Number", serialNumber))
                results.add(Pair("Model", model))
                results.add(Pair("Version", version))
                resultFragment?.setResult(results)
            }
            return super.onPreferenceTreeClick(preference)


        }
    }
}