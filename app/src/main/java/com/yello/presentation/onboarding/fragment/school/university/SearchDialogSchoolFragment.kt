package com.yello.presentation.onboarding.fragment.school.university

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui.base.BindingBottomSheetDialog
import com.example.ui.context.hideKeyboard
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yello.R
import com.yello.databinding.FragmentDialogSchoolBinding
import com.yello.presentation.onboarding.activity.OnBoardingViewModel
import com.yello.util.context.yelloSnackbar

class SearchDialogSchoolFragment :
    BindingBottomSheetDialog<FragmentDialogSchoolBinding>(R.layout.fragment_dialog_school) {
    private var adapter: SchoolAdapter? = null
    private val viewModel by activityViewModels<OnBoardingViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initView()
        setupSchoolData()
        setListWithInfinityScroll()
        recyclerviewScroll()
        seClicktoSchoolform()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun initView() {
        setHideKeyboard()
        binding.etSchoolSearch.doAfterTextChanged { input ->
            viewModel.getSchoolList(input.toString())
        }
        adapter = SchoolAdapter(storeSchool = ::storeSchool)
        binding.rvSchoolList.adapter = adapter
        binding.btnBackDialog.setOnSingleClickListener {
            dismiss()
        }
    }

    private fun setListWithInfinityScroll() {
        binding.rvSchoolList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    recyclerView.layoutManager?.let { layoutManager ->
                        if (!binding.rvSchoolList.canScrollVertically(1) &&
                            layoutManager is LinearLayoutManager &&
                            layoutManager.findLastVisibleItemPosition() == adapter!!.itemCount - 1
                        ) {
                            // viewModel.getSchoolList()
                        }
                    }
                }
            }
        })
    }

    private fun setHideKeyboard() {
        binding.layoutSchoolDialog.setOnSingleClickListener {
            requireContext().hideKeyboard(
                requireView(),
            )
        }
    }

    private fun setupSchoolData() {
        viewModel.schoolData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Failure -> {
                    yelloSnackbar(binding.root, getString(R.string.msg_error))
                }

                is UiState.Loading -> {}
                is UiState.Empty -> {}
                is UiState.Success -> {
                    adapter?.submitList(state.data.schoolList)
                }
            }
        }
    }

    private fun storeSchool(school: String) {
        viewModel.setSchool(school)
        viewModel.clearSchoolData()
        dismiss()
    }
    private fun recyclerviewScroll() {
        binding.rvSchoolList.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    binding.layoutSchoolDialog.requestDisallowInterceptTouchEvent(true)
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun seClicktoSchoolform() {
        binding.tvSchoolAdd.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https:/bit.ly/46Yv0Hc"))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchDialogSchoolFragment()
    }
}