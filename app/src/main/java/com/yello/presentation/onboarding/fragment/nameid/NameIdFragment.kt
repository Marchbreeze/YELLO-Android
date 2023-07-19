package com.yello.presentation.onboarding.fragment.nameid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.ui.base.BindingFragment
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import com.yello.R
import com.yello.databinding.FragmentNameIdBinding
import com.yello.presentation.onboarding.activity.OnBoardingViewModel
import com.yello.util.context.yelloSnackbar

class NameIdFragment : BindingFragment<FragmentNameIdBinding>(R.layout.fragment_name_id) {
    private val viewModel by activityViewModels<OnBoardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        setDeleteBtnClickListener()
        setBtnClickListener()
        setupGetValidYelloId()
    }

    private fun setBtnClickListener() {
        binding.btnNameidNext.setOnSingleClickListener {
            viewModel.getValidYelloId()
        }
        binding.btnNameidBackBtn.setOnSingleClickListener {
            viewModel.navigateToBackPage()
        }
    }

    private fun setDeleteBtnClickListener() {
        binding.btnNameDelete.setOnSingleClickListener {
            binding.etName.text.clear()
        }
        binding.btnIdDelete.setOnClickListener {
            binding.etId.text.clear()
        }
    }

    private fun setupGetValidYelloId() {
        viewModel.getValidYelloId.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Empty -> {
                    yelloSnackbar(binding.root, getString(R.string.msg_error))
                }
                is UiState.Success -> {
                    if (state.data) {
                        viewModel.navigateToNextPage()
                        return@observe
                    }
                    initIdEditTextViewError()
                }
                is UiState.Failure -> {
                    if (state.msg == "404") {
                        viewModel.navigateToNextPage()
                        return@observe
                    }

                    yelloSnackbar(binding.root, getString(R.string.msg_error))
                }
            }
        }
    }

    private fun initIdEditTextViewError() {
        binding.etId.setBackgroundResource(R.drawable.shape_fill_red20_line_semantic_status_red500_rect_8)
        binding.btnIdDelete.setBackgroundResource(R.drawable.ic_onboarding_delete_red)
        binding.tvIdError.text = getString(R.string.name_id_duplicate_id_msg)
        binding.tvIdError.setTextColor(resources.getColor(R.color.semantic_red_500))
    }
}
