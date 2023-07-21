package com.el.yello.presentation.main.yello

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.el.yello.R
import com.el.yello.databinding.FragmentYelloBinding
import com.example.domain.entity.type.YelloState.Lock
import com.example.domain.entity.type.YelloState.Valid
import com.example.domain.entity.type.YelloState.Wait
import com.example.ui.base.BindingFragment
import com.example.ui.fragment.toast
import com.example.ui.view.UiState.Empty
import com.example.ui.view.UiState.Failure
import com.example.ui.view.UiState.Loading
import com.example.ui.view.UiState.Success
import com.el.yello.presentation.main.yello.lock.YelloLockFragment
import com.el.yello.presentation.main.yello.start.YelloStartFragment
import com.el.yello.presentation.main.yello.wait.YelloWaitFragment
import com.el.yello.util.context.yelloSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YelloFragment : BindingFragment<FragmentYelloBinding>(R.layout.fragment_yello) {
    val viewModel by viewModels<YelloViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupYelloState()
    }

    private fun setupYelloState() {
        viewModel.yelloState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Loading -> {}
                is Success -> {
                    when (state.data) {
                        is Lock -> navigateTo<YelloLockFragment>()
                        is Valid -> navigateTo<YelloStartFragment>()
                        is Wait -> navigateTo<YelloWaitFragment>()
                    }
                }

                is Empty -> {
                    yelloSnackbar(
                        binding.root,
                        getString(R.string.msg_failure),
                    )
                }

                is Failure -> {
                    toast(getString(R.string.msg_auto_login_error))
                    restartApp(requireContext())
                }
            }
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        requireActivity().supportFragmentManager.commit {
            replace<T>(R.id.fcv_yello, T::class.java.canonicalName)
        }
    }

    private fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    override fun onResume() {
        super.onResume()

        viewModel.getVoteState()
    }

    companion object {
        @JvmStatic
        fun newInstance() = YelloFragment()
    }
}