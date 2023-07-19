package com.yello.presentation.onboarding.fragment.gender

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import com.example.domain.enum.GenderEnum
import com.example.ui.base.BindingFragment
import com.yello.R
import com.yello.databinding.FragmentGenderBinding
import com.yello.presentation.onboarding.activity.OnBoardingViewModel

class GenderFragment : BindingFragment<FragmentGenderBinding>(R.layout.fragment_gender) {
    private val viewModel by activityViewModels<OnBoardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.male = GenderEnum.M.toString()
        binding.female = GenderEnum.W.toString()

        setupGender()
    }

    private fun setupGender() {
        viewModel._gender.observe(viewLifecycleOwner) { gender ->
            when (gender) {
                GenderEnum.M.toString() -> {
                    binding.btnGenderMale.setBackgroundResource(R.drawable.shape_male700_fill_male300_line_8_rect)
                    binding.btnGenderFemale.setBackgroundResource(R.drawable.shape_grayscales800_fill_8_rect)
                    binding.ivGenderMale.setImageResource(R.drawable.ic_gender_selected_face)
                    binding.ivGenderFemale.setImageResource(R.drawable.ic_gender_unselected_face)
                    binding.tvGenderMale.setTextColor(resources.getColor(R.color.white))
                    binding.tvGenderFemale.setTextColor(Color.parseColor("#FF495057"))
                }
                GenderEnum.W.toString() -> {
                    binding.btnGenderFemale.setBackgroundResource(R.drawable.shape_female700_fill_female300_line_8_rect)
                    binding.btnGenderMale.setBackgroundResource(R.drawable.shape_grayscales800_fill_8_rect)
                    binding.ivGenderMale.setImageResource(R.drawable.ic_gender_unselected_face)
                    binding.ivGenderFemale.setImageResource(R.drawable.ic_gender_selected_face)
                    binding.tvGenderFemale.setTextColor(resources.getColor(R.color.white))
                    binding.tvGenderMale.setTextColor(Color.parseColor("#FF495057"))
                }
            }
        }
    }
}
