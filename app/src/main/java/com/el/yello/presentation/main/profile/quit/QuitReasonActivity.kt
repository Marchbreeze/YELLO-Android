package com.el.yello.presentation.main.profile.quit

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.el.yello.R
import com.el.yello.databinding.ActivityProfileQuitReasonBinding
import com.el.yello.presentation.main.profile.ProfileViewModel
import com.el.yello.presentation.main.profile.manage.ProfileQuitDialog
import com.example.ui.base.BindingActivity
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitReasonActivity :
    BindingActivity<ActivityProfileQuitReasonBinding>(R.layout.activity_profile_quit_reason) {
    private lateinit var quitReasonList: List<String>
    private val viewModel by viewModels<ProfileViewModel>()
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION
    private var isItemClicked: Boolean = false
    private var profileQuitDialog: ProfileQuitDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        initQuitReasonAdapter()
        initBackBtnListener()
        initInviteDialogBtnListener()
    }

    private fun initQuitReasonAdapter() {
        viewModel.addQuitReasonList()
        quitReasonList = viewModel.quitReasonData.value ?: emptyList()
        val adapter = QuitReasonAdapter(storeQuitReason = ::storeQuitReason) { position, clicked ->
            clickedItemPosition = position
            isItemClicked = clicked
            if (isItemClicked) {
                if (clickedItemPosition == 7) {
                   //unClickedButtonUI()
                    clickedButtonUI()
                } else {
                    clickedButtonUI()
                }
            }
        }
        binding.rvQuitReason.adapter = adapter
        adapter.submitList(ArrayList(quitReasonList))
    }

    private fun initInviteDialogBtnListener() {
        binding.btnProfileQuitReasonDone.setOnSingleClickListener {
            profileQuitDialog = ProfileQuitDialog()
            profileQuitDialog?.show(supportFragmentManager, QUIT_DIALOG)
        }
    }

    private fun initBackBtnListener() {
        binding.btnProfileQuitReasonBack.setOnSingleClickListener { finish() }
    }

    private fun clickedButtonUI() {
        binding.btnProfileQuitReasonDone.setBackgroundResource(R.drawable.shape_black_fill_grayscales700_line_100_rect)
        binding.btnProfileQuitReasonDone.setTextColor(ContextCompat.getColor(this, R.color.semantic_red_500))
        binding.btnProfileQuitReasonDone.isEnabled = true
    }

    private fun unClickedButtonUI() {
        binding.btnProfileQuitReasonDone.setBackgroundResource(R.drawable.shape_black_fill_grayscales600_line_100_rect)
        binding.btnProfileQuitReasonDone.setTextColor(ContextCompat.getColor(this, R.color.grayscales_600))
        binding.btnProfileQuitReasonDone.isEnabled = false
    }

    private fun storeQuitReason(reason: String) {
        viewModel.setQuitReason(reason)
    }

    override fun onDestroy() {
        super.onDestroy()
        profileQuitDialog?.dismiss()
    }

    private companion object {
        const val QUIT_DIALOG = "quitDialog"
    }
}
