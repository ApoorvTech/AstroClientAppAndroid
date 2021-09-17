package com.myastrotell.base


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.NetworkStatusMessage
import com.myastrotell.dialogs.AppAlertDialog


abstract class BaseFragment<MyDataBinding : ViewDataBinding, MyViewModel : BaseViewModel> :
    Fragment(), BaseNavigator {

    lateinit var binding: MyDataBinding
    var viewModel: MyViewModel? = null

    private lateinit var rootView: View


    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getBindingVariable(): Int

    abstract fun initViewModel(): MyViewModel?

    abstract fun initVariables()
    abstract fun setObservers()


    private val errorObserver = Observer<BaseResponseModel<*>> {
        hideProgressBar()
        handleApiErrorResponse(it)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        LocaleHelper.setLocale(activity!!)

        if (activity is BaseActivity<*, *>) {
            val newConfig = Configuration(resources.configuration)
            (activity as BaseActivity<*, *>).adjustFontScale(newConfig)
        }

        viewModel = initViewModel()

        initVariables()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        rootView = binding.root
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()

        viewModel?.navigator = this
        viewModel?.setErrorObserver(viewLifecycleOwner, errorObserver)

        setObservers()

    }


    /**
     * method to handle Api Error Response
     * Override this method to provide class level implementation
     */
    @CallSuper
    protected open fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        when (responseModel?.code) {
            ApiStatusCodes.INVALID_TOKEN -> {
                if (isFragmentAdded()) {
                    AppAlertDialog(requireContext(),
                        R.drawable.ic_alert,
                        getString(R.string.session_expired),
                        responseModel.msg,
                        getString(R.string.ok),
                        null,
                        onPositiveBtnClicked = {
                            logout()
                        }).show()
                }
            }

            else -> {
                val message = responseModel?.msg ?: NetworkStatusMessage.SERVER_ERROR.message
                showShortToast(message)
            }
        }
    }


    fun isFragmentAdded(): Boolean {
        return (!requireActivity().isFinishing && !requireActivity().isDestroyed && isAdded)
    }


    fun isUserLoggedIn(): Boolean {
        if (requireActivity() is BaseActivity<*, *>) {
            return (requireActivity() as BaseActivity<*, *>).isUserLoggedIn()
        }
        return false
    }


    fun isGuestUser(): Boolean {
        if (requireActivity() is BaseActivity<*, *>) {
            return (requireActivity() as BaseActivity<*, *>).isGuestUser()
        }
        return false
    }


    fun getWalletBalance(): String {
        if (requireActivity() is BaseActivity<*, *>) {
            return (requireActivity() as BaseActivity<*, *>).getWalletBalance()
        }
        return "0.00"
    }


    fun logout() {
        if (requireActivity() is BaseActivity<*, *>) {
            return (requireActivity() as BaseActivity<*, *>).logout()
        }
    }


    override fun showProgressBar() {
        if (requireActivity() is BaseActivity<*, *>) {
            (requireActivity() as BaseActivity<*, *>).showProgressBar()
        }
    }

    override fun hideProgressBar() {
        if (requireActivity() is BaseActivity<*, *>) {
            (requireActivity() as BaseActivity<*, *>).hideProgressBar()
        }
    }

    override fun showShortToast(msg: String?) {
        if (requireActivity() is BaseActivity<*, *>) {
            (requireActivity() as BaseActivity<*, *>).showShortToast(msg)
        }
    }

    override fun showLongToast(msg: String?) {
        if (requireActivity() is BaseActivity<*, *>) {
            (requireActivity() as BaseActivity<*, *>).showLongToast(msg)
        }
    }

    override fun isNetworkAvailable(): Boolean {
        if (requireActivity() is BaseActivity<*, *>) {
            return (requireActivity() as BaseActivity<*, *>).isNetworkAvailable()
        }
        return false
    }

    override fun showNoNetworkError() {
        if (requireActivity() is BaseActivity<*, *>) {
            (requireActivity() as BaseActivity<*, *>).showNoNetworkError()
        }
    }

    override fun goBack() {
        requireActivity().onBackPressed()
    }

}