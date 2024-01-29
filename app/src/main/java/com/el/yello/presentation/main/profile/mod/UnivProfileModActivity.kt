package com.el.yello.presentation.main.profile.mod

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.el.yello.R
import com.el.yello.databinding.ActivityProfileUnivModBinding
import com.el.yello.util.context.yelloSnackbar
import com.example.ui.activity.navigateTo
import com.example.ui.base.BindingActivity
import com.example.ui.context.toast
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UnivProfileModActivity :
    BindingActivity<ActivityProfileUnivModBinding>(R.layout.activity_profile_univ_mod) {

    private val viewModel by viewModels<UnivProfileModViewModel>()

    private var profileModSchoolBottomSheet: ProfileModSchoolBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUserData()
        initChangeToSchoolBtnListener()
        initSaveBtnListener()
        initBackBtnListener()
        initBottomSheetListener()
        observeGetUserDataResult()
        observeGetIsModValidResult()
        observePostNewProfileResult()
    }

    private fun initUserData() {
        binding.vm = viewModel
        viewModel.getUserDataFromServer()
        viewModel.getIsModValidFromServer()
    }

    private fun initChangeToSchoolBtnListener() {
        binding.btnChangeUnivToSchool.setOnSingleClickListener {
            navigateTo<SchoolProfileModActivity>()
            finish()
        }
    }

    private fun initSaveBtnListener() {
        binding.btnProfileModSave.setOnSingleClickListener {
            if (viewModel.isChanged) {
                yelloSnackbar(binding.root, getString(R.string.profile_mod_no_change))
            } else if (!viewModel.isModAvailable) {
                yelloSnackbar(binding.root, getString(R.string.profile_mod_no_valid))
            } else {
                viewModel.postNewProfileToServer()
            }
        }
    }

    private fun initBackBtnListener() {
        binding.btnProfileModBack.setOnSingleClickListener { finish() }
    }

    private fun initBottomSheetListener() {
        binding.btnSearchSchool.setOnSingleClickListener {
            profileModSchoolBottomSheet = ProfileModSchoolBottomSheet()
            profileModSchoolBottomSheet?.show(supportFragmentManager, DIALOG_SCHOOL)
        }
        binding.btnSearchSubgroup.setOnSingleClickListener {

        }
        binding.btnSearchYear.setOnSingleClickListener {

        }
    }

    private fun observeGetUserDataResult() {
        viewModel.getUserDataResult.flowWithLifecycle(lifecycle).onEach { result ->
            if (!result) yelloSnackbar(binding.root, getString(R.string.msg_error))
        }.launchIn(lifecycleScope)
    }

    private fun observeGetIsModValidResult() {
        viewModel.getIsModValidResult.flowWithLifecycle(lifecycle).onEach { result ->
            if (!result) yelloSnackbar(binding.root, getString(R.string.msg_error))
        }.launchIn(lifecycleScope)
    }

    private fun observePostNewProfileResult() {
        viewModel.postToModProfileResult.flowWithLifecycle(lifecycle).onEach { result ->
            if (result) {
                toast(getString(R.string.profile_mod_success))
                finish()
            } else {
                yelloSnackbar(binding.root, getString(R.string.msg_error))
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (profileModSchoolBottomSheet != null) profileModSchoolBottomSheet?.dismiss()
    }

    private companion object {
        const val DIALOG_SCHOOL = "school"
        const val DIALOG_SUBGROUP = "subgroup"
        const val DIALOG_YEAR = "year"

    }
}