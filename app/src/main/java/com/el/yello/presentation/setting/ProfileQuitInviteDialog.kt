package com.el.yello.presentation.setting

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.el.yello.R
import com.el.yello.databinding.FragmentProfileQuitInviteFriendBinding
import com.el.yello.presentation.main.MainActivity
import com.el.yello.util.manager.AmplitudeManager
import com.example.ui.base.BindingDialogFragment
import com.example.ui.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileQuitInviteDialog :
    BindingDialogFragment<FragmentProfileQuitInviteFriendBinding>(R.layout.fragment_profile_quit_invite_friend) {

    override fun onStart() {
        super.onStart()
        setDialogBackground()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProfileInviteNo.setOnSingleClickListener {
            ProfileQuitDialog().show(parentFragmentManager, QUIT_DIALOG)
            dismiss()
        }
        binding.btnProfileInviteYes.setOnSingleClickListener {
            AmplitudeManager.trackEventWithProperties(EVENT_CLICK_WITHDRAWAL_RECOMMEND)
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(RECOMMEND_FRAGMENT, true)
            }
            startActivity(intent)
            dismiss()
        }
    }

    private fun setDialogBackground() {
        val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
        val dialogHorizontalMargin = (Resources.getSystem().displayMetrics.density * 16) * 2

        dialog?.window?.apply {
            setLayout(
                (deviceWidth - dialogHorizontalMargin * 2).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
    }

    private companion object {
        const val QUIT_DIALOG = "quitDialog"
        private const val RECOMMEND_FRAGMENT = "RecommendFragment"
        private const val EVENT_CLICK_WITHDRAWAL_RECOMMEND = "click_withdrawal_recommend"
    }
}
