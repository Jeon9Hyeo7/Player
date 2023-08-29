package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.databinding.FragmentEditBinding
import com.phoenix.phoenixplayer2.databinding.FragmentParentalControlBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParentalControlFragment(private val type:String): Fragment() {

    private lateinit var mRootActivity: TvActivity
    private lateinit var binding:FragmentParentalControlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootActivity = activity as TvActivity
    }

    companion object{
        const val AUTH_FOR_TUNE = "auth"
        const val AUTH_FOR_UNLOCK = "unlock"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentalControlBinding.inflate(inflater, container, false)
        binding.parentalPositiveButton.setOnClickListener{_->
            run {
                val password = binding.parentalInputText.text.toString()
                if (password == mRootActivity.mProfile.parentPassword) {
                    if (type == AUTH_FOR_TUNE){
                        mRootActivity.tune(true)
                    }
                    else{
                        CoroutineScope(Dispatchers.IO).launch {
                            mRootActivity.unLock()
                        }
                    }
                    mRootActivity.supportFragmentManager.popBackStack()
                }
                else{
                    binding.parentalInputText.setText("")
                    Toast.makeText(requireContext(), "Password Incorrect!", Toast.LENGTH_LONG).show()
                    binding.parentalInputText.requestFocus()
                }
            }
        }
        binding.parentalNegativeButton.setOnClickListener{_->
            run {
                mRootActivity.supportFragmentManager.popBackStack()
            }
        }
        return binding.root
    }



}