package com.el.yello.presentation.pay

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.el.yello.R
import com.el.yello.databinding.ActivityPayBinding
import com.el.yello.presentation.util.BillingCallback
import com.el.yello.presentation.util.BillingManager
import com.example.data.model.request.pay.toRequestPayModel
import com.example.ui.base.BindingActivity
import com.example.ui.context.toast
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PayActivity : BindingActivity<ActivityPayBinding>(R.layout.activity_pay) {

    private val viewModel by viewModels<PayViewModel>()

    private var _adapter: PayAdapter? = null
    private val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }


    private var _manager: BillingManager? = null
    private val manager
        get() = requireNotNull(_manager) { getString(R.string.manager_not_initialized_error_msg) }

    private var productDetailsList = listOf<ProductDetails>()
    private var currentPurchase: Purchase? = null


    private var paySubsDialog: PaySubsDialog? = null
    private var payInAppDialog: PayInAppDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("sangho", "0 : activity created !@!@!@!@@!@!@")
        initView()
        initEvent()
        setBillingManager()
        observeCheckSubsState()
        observeCheckInAppState()
        observeCheckIsSubscribed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIsSubscribed()
        if (viewModel.isFirstCreated) {
            viewModel.isFirstCreated = false
            Log.d("sangho", "1 : first resume ")
        } else {
            startLoadingScreen()
            Log.d("sangho", "2 : not first resume & loading")
        }

    }

    override fun onDestroy() {
        Log.d("sangho", "0 : activity destroyed !@!@!@!@@!@!@")
        super.onDestroy()
        _adapter = null
        _manager?.billingClient?.endConnection()
        _manager = null
        payInAppDialog?.dismiss()
        paySubsDialog?.dismiss()
    }

    // BillingManager 설정 시 BillingClient 연결됨
    private fun setBillingManager() {
        _manager = BillingManager(this, object : BillingCallback {
            override fun onBillingConnected() {
                setProductList()
                Log.d("sangho", "3 : billing manager connected -> setList")
            }

            override fun onSuccess(purchase: Purchase) {
                currentPurchase = purchase
                Log.d("sangho", "4 : purchase success = ${purchase.products}")
                if (purchase.products[0] == "yello_plus_subscribe") {
                    Log.d("sangho", "4-1 : -> check subs")
                    viewModel.checkSubsToServer(purchase.toRequestPayModel())
                } else {
                    Log.d("sangho", "4-2 : -> check inapp")
                    viewModel.checkInAppToServer(purchase.toRequestPayModel())
                }
            }

            override fun onFailure(responseCode: Int) {
                Timber.d(responseCode.toString())
                Log.d("sangho", "5 : purchase failure = ${responseCode}")
            }
        })
    }

    private fun setProductList() {
        lifecycleScope.launch {
            manager.getProductDetails() { list ->
                productDetailsList = list
                Log.d("sangho", "6 : getProductList = ${list}")
            }
        }
    }

    private fun initView() {
        Log.d("sangho", "7 : init adapter & isFirstCreated set")
        _adapter = PayAdapter()
        binding.vpBanner.adapter = adapter
        binding.dotIndicator.setViewPager(binding.vpBanner)
        binding.tvOriginalPrice.paintFlags =
            binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewModel.isFirstCreated = true
    }

    private fun initEvent() {
        binding.clSubscribe.setOnSingleClickListener {
            viewModel.payCheck(0)
            productDetailsList.withIndex().find { it.value.productId == YELLO_PLUS }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "8 : click = ${productDetails.value.name} -> purchase")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckOne.setOnSingleClickListener {
            viewModel.payCheck(1)
            productDetailsList.withIndex().find { it.value.productId == YELLO_ONE }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "9 : click = ${productDetails.value.name} -> purchase")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckTwo.setOnSingleClickListener {
            viewModel.payCheck(2)
            productDetailsList.withIndex().find { it.value.productId == YELLO_TWO }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "10 : click = ${productDetails.value.name} -> purchase")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckFive.setOnSingleClickListener {
            viewModel.payCheck(3)
            productDetailsList.withIndex().find { it.value.productId == YELLO_FIVE }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "11 : click = ${productDetails.value.name} -> purchase")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.ivBack.setOnSingleClickListener {
            finish()
        }
    }

    private fun observeCheckSubsState() {
        viewModel.postSubsCheckState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    Log.d("sangho", "12 : sub success : @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                    stopLoadingScreen()
                    paySubsDialog = PaySubsDialog()
                    paySubsDialog?.show(supportFragmentManager, DIALOG_SUBS)
                }

                is UiState.Failure -> {
                    Log.d("sangho", "13 : sub failure : @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                    toast("잘못된 접근입니다.")
                    stopLoadingScreen()
                }

                is UiState.Loading -> {}

                is UiState.Empty -> {}
            }
        }
    }

    private fun observeCheckInAppState() {
        viewModel.postInAppCheckState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    Log.d(
                        "sangho",
                        "13: inapp success : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"
                    )
                    stopLoadingScreen()
                    viewModel.currentInAppItem = state.data?.ticketCount ?: 0
                    payInAppDialog = PayInAppDialog()
                    payInAppDialog?.show(supportFragmentManager, DIALOG_IN_APP)
                }

                is UiState.Failure -> {
                    Log.d(
                        "sangho",
                        "14 : inapp failure : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"
                    )
                    toast("잘못된 접근입니다.")
                    stopLoadingScreen()
                }

                is UiState.Loading -> {}

                is UiState.Empty -> {}
            }
        }
    }

    private fun startLoadingScreen() {
        binding.layoutPayCheckLoading.visibility = View.VISIBLE
        this.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
    }

    private fun stopLoadingScreen() {
        binding.layoutPayCheckLoading.visibility = View.GONE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun observeCheckIsSubscribed() {
        viewModel.getIsSubscribedState.observe(this) { state ->
            Log.d("sangho", "15 : observe subs state")
            when (state) {
                is UiState.Success -> {
                    if (state.data?.subscribe == "ACTIVE") {
                        binding.layoutShowSubs.visibility = View.VISIBLE
                    } else {
                        binding.layoutShowSubs.visibility = View.GONE
                    }
                }

                is UiState.Failure -> {
                    binding.layoutShowSubs.visibility = View.GONE
                    toast("구독 여부 가져오기 실패")
                }

                is UiState.Loading -> {}

                is UiState.Empty -> {}
            }
        }
    }

    companion object {
        const val YELLO_PLUS = "yello_plus_subscribe"
        const val YELLO_ONE = "yello_ticket_one"
        const val YELLO_TWO = "yello_ticket_two"
        const val YELLO_FIVE = "yello_ticket_five"

        const val DIALOG_SUBS = "subsDialog"
        const val DIALOG_IN_APP = "inAppDialog"
    }
}