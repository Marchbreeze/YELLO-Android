package com.el.yello.presentation.setting

import android.content.Intent
import android.os.Bundle
import com.el.yello.R
import com.el.yello.databinding.ActivityProfileQuitTwoBinding
import com.el.yello.util.manager.AmplitudeManager
import com.example.ui.base.BindingActivity
import com.example.ui.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ProfileQuitTwoActivity :
    BindingActivity<ActivityProfileQuitTwoBinding>(R.layout.activity_profile_quit_two) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBackBtnListener()
        initQuitBtnListener()
    }

    private fun initBackBtnListener() {
        binding.btnProfileQuitBack.setOnSingleClickListener { finish() }
    }

    private fun initQuitBtnListener() {
        binding.btnProfileQuitForSure.setOnSingleClickListener {
            AmplitudeManager.trackEventWithProperties(
                EVENT_CLICK_PROFILE_WITHDRAWAL,
                JSONObject().put(NAME_WITHDRAWAL_BUTTON, VALUE_WITHDRAWAL_THREE),
            )
            Intent(this, ProfileQuitReasonActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
            }
        }
    }
    companion object {
        private const val EVENT_CLICK_PROFILE_WITHDRAWAL = "click_profile_withdrawal"
        private const val NAME_WITHDRAWAL_BUTTON = "withdrawal_button"
        private const val VALUE_WITHDRAWAL_THREE = "withdrawal3"
    }
}
