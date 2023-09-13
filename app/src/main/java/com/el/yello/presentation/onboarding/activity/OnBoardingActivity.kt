package com.el.yello.presentation.onboarding.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.el.yello.R
import com.el.yello.databinding.ActivityOnboardingBinding
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_EMAIL
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_GENDER
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_KAKAO_ID
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_NAME
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_PROFILE_IMAGE
import com.el.yello.presentation.auth.SocialSyncActivity
import com.el.yello.presentation.tutorial.TutorialAActivity
import com.example.ui.base.BindingActivity
import com.example.ui.context.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity :
    BindingActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {

    private val viewModel by viewModels<OnBoardingViewModel>()

    val navController = findNavController(R.id.nav_main_fragment)
    val currentDestinationId = navController.currentDestination?.id

    private var backPressedTime: Long = 0

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (currentDestinationId) {
                R.id.universityInfoFragment -> startSocialSyncActivity()

                R.id.codeFragment -> {
                    if (System.currentTimeMillis() - backPressedTime >= BACK_PRESSED_INTERVAL) {
                        backPressedTime = System.currentTimeMillis()
                        toast(getString(R.string.main_toast_back_pressed))
                    } else {
                        finish()
                    }
                }

                else -> {
                    navController.popBackStack()
                    progressBarMinus()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtraData()
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    fun onBackButtonClicked() {
        if (currentDestinationId == R.id.universityInfoFragment) {
            startSocialSyncActivity()
        } else {
            navController.popBackStack()
            progressBarMinus()
        }
    }

    private fun getIntentExtraData() {
        intent.apply {
            viewModel.kakaoId = getLongExtra(EXTRA_KAKAO_ID, -1).toString()
            viewModel.email = getStringExtra(EXTRA_EMAIL) ?: ""
            viewModel.profileImg = getStringExtra(EXTRA_PROFILE_IMAGE) ?: ""
            viewModel.name = getStringExtra(EXTRA_NAME) ?: ""
            viewModel.gender = getStringExtra(EXTRA_GENDER) ?: ""
        }
    }

    fun progressBarPlus() {
        viewModel.plusCurrentPercent()
        val animator = ObjectAnimator.ofInt(
            binding.onboardingProgressbar,
            "progress",
            binding.onboardingProgressbar.progress,
            viewModel.currentPercent
        )
        animator.duration = 300
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    fun progressBarMinus() {
        viewModel.minusCurrentPercent()
        val animator = ObjectAnimator.ofInt(
            binding.onboardingProgressbar,
            "progress",
            binding.onboardingProgressbar.progress,
            viewModel.currentPercent
        )
        animator.duration = 300
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    fun hideViews() {
        binding.backBtn.visibility = View.INVISIBLE
        binding.onboardingProgressbar.visibility = View.INVISIBLE
    }

    fun hideBackBtn() {
        binding.backBtn.visibility = View.INVISIBLE
    }

    fun showBackBtn() {
        binding.backBtn.visibility = View.VISIBLE
    }

    fun startSocialSyncActivity() {
        val intent = Intent(this, SocialSyncActivity::class.java)
        startActivity(intent)
    }

    fun endTutorialActivity() {
        val intent = TutorialAActivity.newIntent(this, true)
        intent.putExtra("codeTextEmpty", viewModel.isCodeTextEmpty())
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val BACK_PRESSED_INTERVAL = 2000
    }
}
