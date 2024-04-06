package com.el.yello.presentation.setting

import android.content.Intent
import android.os.Bundle
import com.el.yello.R
import com.el.yello.databinding.ActivityProfileQuitOneBinding
import com.el.yello.util.manager.AmplitudeManager
import com.example.ui.base.BindingActivity
import com.example.ui.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ProfileQuitOneActivity :
    BindingActivity<ActivityProfileQuitOneBinding>(R.layout.activity_profile_quit_one) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBackBtnListener()
        initReturnBtnListener()
        initQuitBtnListener()
    }

    private fun initBackBtnListener() {
        binding.btnProfileQuitForSureBack.setOnSingleClickListener { finish() }
    }

    private fun initReturnBtnListener() {
        binding.btnProfileQuitReturn.setOnSingleClickListener { finish() }
    }

    private fun initQuitBtnListener() {
        binding.btnProfileQuitResume.setOnSingleClickListener {
            AmplitudeManager.trackEventWithProperties(
                EVENT_CLICK_PROFILE_WITHDRAWAL,
                JSONObject().put(NAME_WITHDRAWAL_BUTTON, VALUE_WITHDRAWAL_TWO),
            )
            Intent(this, ProfileQuitTwoActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
            }
        }
    }

    companion object {
        private const val EVENT_CLICK_PROFILE_WITHDRAWAL = "click_profile_withdrawal"
        private const val NAME_WITHDRAWAL_BUTTON = "withdrawal_button"
        private const val VALUE_WITHDRAWAL_TWO = "withdrawal2"
    }
}
