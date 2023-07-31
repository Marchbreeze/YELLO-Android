package com.el.yello.presentation.main.profile.info

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.el.yello.R
import com.el.yello.databinding.FragmentProfileFriendItemBottomSheetBinding
import com.el.yello.presentation.main.profile.ProfileViewModel
import com.example.ui.base.BindingBottomSheetDialog
import com.example.ui.view.setOnSingleClickListener

class ProfileFriendItemBottomSheet :
    BindingBottomSheetDialog<FragmentProfileFriendItemBottomSheetBinding>(R.layout.fragment_profile_friend_item_bottom_sheet) {

    private val profileFriendDeleteBottomSheet: ProfileFriendDeleteBottomSheet =
        ProfileFriendDeleteBottomSheet()
    private val viewModel by activityViewModels<ProfileViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.isItemBottomSheetRunning = true
        initDeleteBtnListener()
        setItemImage()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.isItemBottomSheetRunning = false
    }

    private fun setItemImage() {
        binding.ivProfileFriendThumbnail.load(viewModel.clickedItemThumbnail.value) {
            transformations(CircleCropTransformation())
        }
    }

    // 다음 바텀시트 출력
    private fun initDeleteBtnListener() {
        binding.btnProfileFriendDelete.setOnSingleClickListener {
            profileFriendDeleteBottomSheet.show(parentFragmentManager, DELETE_BOTTOM_SHEET)
            dismiss()
        }
    }

    private companion object {
        const val DELETE_BOTTOM_SHEET = "deleteBottomSheet"
    }
}
