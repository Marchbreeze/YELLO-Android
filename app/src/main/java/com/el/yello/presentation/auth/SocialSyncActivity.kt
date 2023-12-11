package com.el.yello.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.el.yello.R
import com.el.yello.databinding.ActivitySocialSyncBinding
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_EMAIL
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_GENDER
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_KAKAO_ID
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_NAME
import com.el.yello.presentation.auth.SignInActivity.Companion.EXTRA_PROFILE_IMAGE
import com.el.yello.presentation.onboarding.activity.EditNameActivity
import com.el.yello.presentation.onboarding.fragment.checkName.CheckNameDialog
import com.el.yello.util.amplitude.AmplitudeUtils
import com.el.yello.util.context.yelloSnackbar
import com.example.ui.base.BindingActivity
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SocialSyncActivity :
    BindingActivity<ActivitySocialSyncBinding>(R.layout.activity_social_sync) {

    private val viewModel by viewModels<SocialSyncViewModel>()

    private var checkNameDialog: CheckNameDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSocialSyncBtnListener()
        observeFriendsAccessState()
    }

    private fun initSocialSyncBtnListener() {
        binding.btnSocialSync.setOnSingleClickListener {
            AmplitudeUtils.trackEventWithProperties("click_onboarding_kakao_friends")
            viewModel.getKakaoFriendsList()
        }
    }

    // 친구목록 동의만 받고 온보딩 뷰로 이동
    private fun observeFriendsAccessState() {
        viewModel.getFriendListState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> startCheckNameDialog()
                is UiState.Failure -> yelloSnackbar(binding.root, getString(R.string.msg_error))
                is UiState.Empty -> return@onEach
                is UiState.Loading -> return@onEach
            }
        }.launchIn(lifecycleScope)
    }

    private fun startCheckNameDialog() {
        intent.apply {
            val kakaoId = getLongExtra(EXTRA_KAKAO_ID, -1)
            val email = getStringExtra(EXTRA_EMAIL)
            val profileImage = getStringExtra(EXTRA_PROFILE_IMAGE)
            val name = getStringExtra(EXTRA_NAME)
            val gender = getStringExtra(EXTRA_GENDER)
            val bundle = Bundle().apply {
                putLong("EXTRA_KAKAO_ID", kakaoId)
                putString("EXTRA_NAME", name)
                putString("EXTRA_GENDER", gender)
                putString("EXTRA_EMAIL", email)
                putString("EXTRA_PROFILE_IMAGE", profileImage)
            }
            if (name.toString().isBlank() || name.toString().isEmpty() || name.toString().isNullOrEmpty() || name.toString().isNullOrBlank()) {
                val bundle = Bundle().apply {
                    putLong("EXTRA_KAKAO_ID", kakaoId)
                    putString("EXTRA_NAME", name)
                    putString("EXTRA_GENDER", gender)
                    putString("EXTRA_EMAIL", email)
                    putString("EXTRA_PROFILE_IMAGE", profileImage)
                }
                val intent = Intent(SocialSyncActivity(), EditNameActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            binding.tvSocialSyncTitle.visibility = View.GONE
            binding.tvSocialSyncSubtitle.visibility = View.GONE
            binding.ivSocialSync.visibility = View.GONE
            binding.btnSocialSync.visibility = View.GONE
            checkNameDialog?.arguments = bundle
            checkNameDialog?.show(supportFragmentManager, SignInActivity.CHECK_NAME_DIALOG)
        }
    }
}
