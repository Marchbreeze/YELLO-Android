package com.el.yello.presentation.main.look

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.el.yello.R
import com.el.yello.databinding.FragmentLookBinding
import com.el.yello.util.context.yelloSnackbar
import com.example.domain.entity.LookListModel
import com.example.ui.base.BindingFragment
import com.example.ui.view.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LookFragment : BindingFragment<FragmentLookBinding>(R.layout.fragment_look) {

    private var _adapter: LookAdapter? = null
    private val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by viewModels<LookViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapterWithFirstList()
        observeLookListDataState()

    }

    override fun onDestroyView() {
        _adapter = null
        super.onDestroyView()
    }

    private fun initAdapterWithFirstList() {
        _adapter = LookAdapter()
        binding.rvLook.adapter = adapter
        viewModel.addLookListFromServer(page = 0)
    }

    // 둘러보기 목록 서버 통신 성공 시 어댑터에 리스트 추가
    private fun observeLookListDataState() {
        viewModel.getLookListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.submitList(state.data?.friendVotes)
                }

                is UiState.Failure -> {
                    yelloSnackbar(requireView(), getString(R.string.look_error_friend_list))
                }

                is UiState.Empty -> {}

                is UiState.Loading -> {}
            }
        }
    }

    fun scrollToTop() {
        binding.rvLook.smoothScrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LookFragment()
    }
}