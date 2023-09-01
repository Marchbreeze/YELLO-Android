package com.el.yello.presentation.main.recommend.school

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.el.yello.R
import com.el.yello.databinding.FragmentRecommendSchoolBinding
import com.el.yello.presentation.main.recommend.RecommendInviteDialog
import com.el.yello.presentation.main.recommend.list.RecommendAdapter
import com.el.yello.presentation.main.recommend.list.RecommendItemDecoration
import com.el.yello.presentation.main.recommend.list.RecommendViewHolder
import com.el.yello.presentation.util.BaseLinearRcvItemDeco
import com.el.yello.util.amplitude.AmplitudeUtils
import com.el.yello.util.context.yelloSnackbar
import com.example.domain.entity.RecommendModel.RecommendFriend
import com.example.ui.base.BindingFragment
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class RecommendSchoolFragment :
    BindingFragment<FragmentRecommendSchoolBinding>(R.layout.fragment_recommend_school) {

    private var _adapter: RecommendAdapter? = null
    private val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by viewModels<RecommendSchoolViewModel>()

    private var recommendInviteYesFriendDialog: RecommendInviteDialog? = null
    private var recommendInviteNoFriendDialog: RecommendInviteDialog? = null

    private lateinit var friendsList: List<RecommendFriend>

    private lateinit var itemDivider: RecommendItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirstList()
        initInviteBtnListener()
        initPullToScrollListener()
        setItemDecoration()
        setAdapterWithClickListener()
        setListWithInfinityScroll()
        observeAddListState()
        observeAddFriendState()
        setDeleteAnimation()
        AmplitudeUtils.trackEventWithProperties("view_recommend_school")
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isFirstResume) {
            adapter.clearList()
            viewModel.setFirstPageLoading()
            viewModel.addListFromServer()
        }
        viewModel.isFirstResume = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        dismissDialog()
    }

    private fun initInviteBtnListener() {
        binding.layoutInviteFriend.setOnSingleClickListener {
            recommendInviteYesFriendDialog =
                RecommendInviteDialog.newInstance(viewModel.getYelloId(), SCHOOL_YES_FRIEND)
            AmplitudeUtils.trackEventWithProperties(
                "click_invite",
                JSONObject().put("invite_view", SCHOOL_YES_FRIEND)
            )
            recommendInviteYesFriendDialog?.show(parentFragmentManager, INVITE_DIALOG)
        }

        binding.btnRecommendNoFriend.setOnSingleClickListener {
            recommendInviteNoFriendDialog =
                RecommendInviteDialog.newInstance(viewModel.getYelloId(), SCHOOL_NO_FRIEND)
            AmplitudeUtils.trackEventWithProperties(
                "click_invite",
                JSONObject().put("invite_view", SCHOOL_NO_FRIEND)
            )
            recommendInviteNoFriendDialog?.show(parentFragmentManager, INVITE_DIALOG)
        }
    }

    private fun initFirstList() {
        viewModel.isFirstResume = true
        viewModel.setFirstPageLoading()
        viewModel.addListFromServer()
    }

    private fun initPullToScrollListener() {
        binding.layoutRecommendSchoolSwipe.apply {
            setOnRefreshListener {
                lifecycleScope.launch {
                    adapter.clearList()
                    viewModel.setFirstPageLoading()
                    viewModel.addListFromServer()
                    delay(200)
                    binding.layoutRecommendSchoolSwipe.isRefreshing = false
                }
            }
            setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.grayscales_700))
            setColorSchemeColors(ContextCompat.getColor(context, R.color.grayscales_500))
        }
    }

    private fun setItemDecoration() {
        itemDivider = RecommendItemDecoration(requireContext())
        binding.rvRecommendSchool.addItemDecoration(itemDivider)
        binding.rvRecommendSchool.addItemDecoration(
            BaseLinearRcvItemDeco(
                0,
                0,
                0,
                0,
                0,
                RecyclerView.VERTICAL,
                12,
            ),
        )
    }

    // 처음 리스트 설정 및 어댑터 클릭 리스너 설정
    private fun setAdapterWithClickListener() {
        _adapter = RecommendAdapter { recommendModel, position, holder ->
            viewModel.setPositionAndHolder(position, holder)
            viewModel.addFriendToServer(recommendModel.id.toLong())
        }
        binding.rvRecommendSchool.adapter = adapter
    }

    // 무한 스크롤 구현
    private fun setListWithInfinityScroll() {
        binding.rvRecommendSchool.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    recyclerView.layoutManager?.let { layoutManager ->
                        if (!binding.rvRecommendSchool.canScrollVertically(1) &&
                            layoutManager is LinearLayoutManager &&
                            layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1
                        ) {
                            viewModel.addListFromServer()
                        }
                    }
                }
            }
        })
    }

    // 추천친구 리스트 추가 서버 통신 성공 시 어댑터에 리스트 추가
    private fun observeAddListState() {
        viewModel.postFriendsListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    if (state.data?.friends?.isEmpty() == true && adapter.itemCount == 0) {
                        showNoFriendScreen()
                    } else {
                        showFriendListScreen()
                        friendsList = state.data?.friends ?: listOf()
                        adapter.addItemList(friendsList)
                    }
                }

                is UiState.Failure -> {
                    showShimmerScreen()
                    yelloSnackbar(
                        requireView(),
                        getString(R.string.recommend_error_school_friend_connection),
                    )
                }

                is UiState.Loading -> {
                    showShimmerScreen()
                }

                is UiState.Empty -> {}
            }
        }
    }

    // 친구 추가 서버 통신 성공 시 리스트에서 아이템 삭제 & 서버 통신 중 액티비티 클릭 방지
    private fun observeAddFriendState() {
        viewModel.addFriendState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    val position = viewModel.itemPosition
                    val holder = viewModel.itemHolder
                    if (position != null && holder != null) {
                        removeItemWithAnimation(holder, position)
                    } else {
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }

                is UiState.Failure -> {
                    yelloSnackbar(
                        requireView(),
                        getString(R.string.recommend_error_add_friend_connection),
                    )
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                is UiState.Loading -> {
                    activity?.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    )
                }

                is UiState.Empty -> {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun setDeleteAnimation() {
        binding.rvRecommendSchool.itemAnimator = object : DefaultItemAnimator() {
            override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
                holder.itemView.animation =
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_out_right)
                return super.animateRemove(holder)
            }
        }
    }

    private fun dismissDialog() {
        if (recommendInviteYesFriendDialog?.isAdded == true) recommendInviteYesFriendDialog?.dismiss()
        if (recommendInviteNoFriendDialog?.isAdded == true) recommendInviteNoFriendDialog?.dismiss()
    }

    // 삭제 시 체크 버튼으로 전환 후 0.3초 뒤 애니메이션 적용
    private fun removeItemWithAnimation(holder: RecommendViewHolder, position: Int) {
        lifecycleScope.launch {
            changeToCheckIcon(holder)
            delay(300)
            binding.rvRecommendSchool.removeItemDecoration(itemDivider)
            adapter.removeItem(position)
            delay(500)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.rvRecommendSchool.addItemDecoration(itemDivider)
            if (adapter.itemCount == 0) {
                showNoFriendScreen()
            }
        }
    }

    private fun changeToCheckIcon(holder: RecommendViewHolder) {
        holder.binding.btnRecommendItemAdd.visibility = View.INVISIBLE
        holder.binding.btnRecommendItemAddPressed.visibility = View.VISIBLE
    }

    private fun showShimmerScreen() {
        binding.layoutRecommendFriendsList.isVisible = true
        binding.layoutRecommendNoFriendsList.isVisible = false
        binding.shimmerFriendList.startShimmer()
        binding.shimmerFriendList.visibility = View.VISIBLE
        binding.rvRecommendSchool.visibility = View.GONE
    }

    private fun showFriendListScreen() {
        binding.layoutRecommendFriendsList.isVisible = true
        binding.layoutRecommendNoFriendsList.isVisible = false
        binding.shimmerFriendList.stopShimmer()
        binding.shimmerFriendList.visibility = View.GONE
        binding.rvRecommendSchool.visibility = View.VISIBLE
    }

    private fun showNoFriendScreen() {
        binding.layoutRecommendFriendsList.isVisible = false
        binding.layoutRecommendNoFriendsList.isVisible = true
        binding.shimmerFriendList.stopShimmer()
    }

    fun scrollToTop() {
        binding.rvRecommendSchool.smoothScrollToPosition(0)
    }

    private companion object {
        const val INVITE_DIALOG = "inviteDialog"

        const val SCHOOL_NO_FRIEND = "recommend_school_nofriend"
        const val SCHOOL_YES_FRIEND = "recommend_school_yesfriend"
    }
}