package com.yello.presentation.main.profile.manage

import android.os.Bundle
import androidx.activity.viewModels
import com.example.ui.base.BindingActivity
import com.example.ui.view.setOnSingleClickListener
import com.yello.R
import com.yello.databinding.ActivityProfileQuitForSureBinding
import com.yello.presentation.main.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileQuitForSureActivity :
    BindingActivity<ActivityProfileQuitForSureBinding>(R.layout.activity_profile_quit_for_sure) {
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initReturnButton()
        initInviteDialogButtonListener()
    }

    private fun initReturnButton() {
        binding.btnProfileQuitForSureBack.setOnSingleClickListener {
            finish()
        }
    }

    private fun initInviteDialogButtonListener() {
        binding.btnProfileQuitForSure.setOnSingleClickListener {
            ProfileQuitDialog().show(supportFragmentManager, DIALOG)
        }
    }

    private companion object {
        const val DIALOG = "dialog"
    }
}
