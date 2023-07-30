package com.el.yello.presentation.main.profile.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.el.yello.R
import com.el.yello.databinding.ActivityProfileManageBinding
import com.el.yello.presentation.main.profile.ProfileViewModel
import com.el.yello.util.context.yelloSnackbar
import com.example.ui.base.BindingActivity
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileManageActivity :
    BindingActivity<ActivityProfileManageBinding>(R.layout.activity_profile_manage) {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBackBtnListener(this)
        initQuitBtnListener()
        initCenterBtnListener()
        initPrivacyBtnListener()
        initServiceBtnListener()
        initLogoutBtnListener()
        observeKakaoLogoutState()
    }

    private fun initCenterBtnListener() {
        binding.btnProfileManageCenter.setOnSingleClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://yell0.notion.site/YELLO-34028220a873416b91d5d2f1cd827432?pvs=4"),
                ),
            )
        }
    }

    private fun initPrivacyBtnListener() {
        binding.btnProfileManagePrivacy.setOnSingleClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://yell0.notion.site/97f57eaed6c749bbb134c7e8dc81ab3f"),
                ),
            )
        }
    }

    private fun initServiceBtnListener() {
        binding.btnProfileManageService.setOnSingleClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://yell0.notion.site/2afc2a1e60774dfdb47c4d459f01b1d9"),
                ),
            )
        }
    }

    private fun initLogoutBtnListener() {
        binding.btnProfileManageLogout.setOnSingleClickListener {
           viewModel.logoutKakaoAccount()
        }
    }

    private fun initBackBtnListener(activity: Activity) {
        binding.btnProfileManageBack.setOnSingleClickListener {
            activity.finish()
        }
    }

    private fun initQuitBtnListener() {
        binding.btnProfileManageQuit.setOnSingleClickListener {
            Intent(this, ProfileQuitActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
            }
        }
    }

    private fun logoutKakaoAccount() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.d(getString(R.string.profile_error_logout) + ": $error")
            } else {
                lifecycleScope.launch {
                    viewModel.clearLocalInfo()
                    delay(500)
                    restartApp(this@ProfileManageActivity)
                }
            }
        }
    }

    private fun observeKakaoLogoutState() {
        viewModel.kakaoLogoutState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    lifecycleScope.launch {
                        viewModel.clearLocalInfo()
                        delay(500)
                        restartApp(this@ProfileManageActivity)
                    }
                }

                is UiState.Failure -> {
                    yelloSnackbar(binding.root, getString(R.string.msg_error))
                    Timber.d(getString(R.string.profile_error_logout) + ": ${state.msg}")
                }

                is UiState.Empty -> {}

                is UiState.Loading -> {}
            }
        }
    }

    private fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}
