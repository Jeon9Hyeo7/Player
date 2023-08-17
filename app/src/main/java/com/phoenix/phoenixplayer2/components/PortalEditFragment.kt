package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.api.DeviceManager
import com.phoenix.phoenixplayer2.databinding.FragmentEditBinding
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.enums.EPGMode
import com.phoenix.phoenixplayer2.model.enums.EPGOffset
import com.phoenix.phoenixplayer2.model.enums.GroupChannelNumbering
import com.phoenix.phoenixplayer2.model.enums.MacType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PortalEditFragment(private var portal: Portal? = null) : Fragment(){

    private lateinit var binding: FragmentEditBinding
    private var edit:Boolean = false


    companion object {
        private const val TAG:String = "PortalEditFragment"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = portal != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        setSpinners()
        setCheckBox()

        setButtons()


        return binding.root
    }

    private fun setButtons() {
        binding.buttonEdit.setOnClickListener {
            createPortal()
        }
    }

    private fun setCheckBox() {
        val login = binding.checkBox
        login.setOnClickListener {
            val checkBox = it as CheckBox
            if (checkBox.isChecked) {
                binding.userName.visibility = VISIBLE
                binding.userPw.visibility = VISIBLE
            } else {
                binding.userName.visibility = GONE
                binding.userPw.visibility = GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.portalNameInput.requestFocus()


    }

    private fun setSpinners(){
        val epgModes = EPGMode.values().toList()
        val epgOffsets = EPGOffset.values().toList()
        val grouping = GroupChannelNumbering.values().toList()
        val macTypes = MacType.values().toList()
        binding.spinnerEpgMode.adapter = getSpinnerAdapter(epgModes)
        binding.spinnerEpgOffset.adapter = getSpinnerAdapter(epgOffsets)
        binding.spinnerGrouping.adapter = getSpinnerAdapter(grouping)
        binding.spinnerMacType.adapter = getSpinnerAdapter(macTypes)

    }

    private fun getSpinnerAdapter(list: List<*>): ArrayAdapter<*>{
        return ArrayAdapter(requireContext(), R.layout.spinner_textview, list)
    }


    /**
     * Because the API I use may be different from the Portal API you want (+ my company's copyrighted elements as well)
     * the analysis part of the API has not been uploaded to the repository
     * You just need to formally implement a class called "ConnectManager"(which is just an arbitrary class name)
     * */

    private fun createPortal(){
        var connectManager = ConnectManager()

        val name = binding.portalNameInput.text.toString()
        val clientUrl = binding.portalUrlInput.text.toString()
        val login = binding.checkBox.isChecked
        val userName = binding.userName.toString()
        val password = binding.userPw.toString()
        val epgMode = binding.spinnerEpgMode.selectedItem as EPGMode
        val epgOffset = binding.spinnerEpgOffset.selectedItem as EPGOffset
        val grouping = binding.spinnerGrouping.selectedItem as GroupChannelNumbering
        val macType = binding.spinnerMacType.selectedItem as MacType
        val defaultMacAddress = DeviceManager().getMacAddress()
        val macAddress:String = when (macType){
            MacType.Default -> {
                macType.getAddress() + defaultMacAddress.substring(8, 17)
            }
            MacType.Alternative -> {defaultMacAddress}
            MacType.Custom -> {""}
        }

        portal = Portal(
            title = name,
            url = clientUrl,
            log_req = login,
            user_ID =  userName,
            user_PW = password,
            EPG_MODE = epgMode,
            EPG_OFFSET = epgOffset,
            group_Numbering = grouping,
            MAC_TYPE = macType,
            macAddress = macAddress)



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverUrl = connectManager.getServerUrl(portalUrl = portal!!.url)
                val token = connectManager.getToken(serverUrl, macAddress)
                connectManager = ConnectManager(serverUrl, macAddress, token)
                val expDate = connectManager.getMainInfo()!!.expDate
                portal = portal!!.copy(
                    serverUrl = serverUrl,
                    token = token,
                    exp_date = expDate
                )
                val insert = portal!!
                (activity as MainActivity).getRepository().insert(insert)
                withContext(Dispatchers.Main){
                    (activity as MainActivity).supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Login information is incorrect. Please review your input.", Toast.LENGTH_LONG).show()
                }

            }

        }












    }



}