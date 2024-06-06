package com.el.yello.presentation.setting

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.el.yello.R
import com.el.yello.databinding.FragmentProfileQuitInviteFriendBinding
import com.example.ui.base.BindingDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileQuitInviteDialog :
    BindingDialogFragment<FragmentProfileQuitInviteFriendBinding>(R.layout.fragment_profile_quit_invite_friend) {

    override fun onStart() {
        super.onStart()
        setDialogBackground()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
