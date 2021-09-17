package com.myastrotell.base


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.myastrotell.BaseApplication
import com.myastrotell.R
import com.myastrotell.data.*
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.ui.login.LoginSignUpActivity
import java.util.*
import kotlin.concurrent.thread


abstract class BaseActivity<MyDataBinding : ViewDataBinding, MyViewModel : BaseViewModel> :
    AppCompatActivity(), BaseNavigator {

    lateinit var binding: MyDataBinding
    var viewModel: MyViewModel? = null

    private var mProgressDialog: Dialog? = null


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

        Locale.setDefault(Locale("en"))

        val newConfig = Configuration(resources?.configuration)
        adjustFontScale(newConfig)

        performDataBinding()

        initVariables()

        viewModel?.navigator = this
        viewModel?.setErrorObserver(this, errorObserver)

        setObservers()

    }


    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId())

        viewModel = initViewModel()

        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()

    }


    /**
     * method to handle Api Error Response
     * Override this method to provide class level implementation
     */
    @CallSuper
    protected open fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        when (responseModel?.code) {
            ApiStatusCodes.INVALID_TOKEN -> {
                AppAlertDialog(this,
                    R.drawable.ic_alert,
                    getString(R.string.session_expired),
                    responseModel.msg,
                    getString(R.string.ok),
                    null,
                    onPositiveBtnClicked = {
                        logout()
                    }).show()
            }

            else -> {
                val message = responseModel?.msg ?: NetworkStatusMessage.SERVER_ERROR.message
                showShortToast(message)
            }
        }
    }


    fun isUserLoggedIn(): Boolean {
        return DataManager.getBooleanFromPreference(PreferenceManager.IS_LOGGED_IN)
    }


    fun isGuestUser(): Boolean {
        return DataManager.getBooleanFromPreference(PreferenceManager.IS_GUEST_USER)
    }


    fun getWalletBalance(): String {
        var balance = DataManager.getStringFromPreference(PreferenceManager.WALLET_BALANCE)
        if (balance.isBlank())
            balance = "0.0"
        return String.format("%.2f", balance.toDouble())
    }


    fun showLoginDialog() {
        AppAlertDialog(this,
            R.drawable.ic_alert,
            getString(R.string.login_required),
            getString(R.string.login_required_to_access_feature),
            getString(R.string.confirm),
            getString(R.string.cancel),
            onPositiveBtnClicked = {
                val intent = Intent(this, LoginSignUpActivity::class.java)
                startActivity(intent)
            }
        ).show()
    }


    override fun showProgressBar() {
        if (!isFinishing) {
            hideProgressBar()
            mProgressDialog = Dialog(this)
            mProgressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
            mProgressDialog?.setContentView(view)

            mProgressDialog?.setCancelable(false)

            mProgressDialog?.window?.let {
                it.setBackgroundDrawable(
                    ContextCompat.getDrawable(this, android.R.color.transparent)
                )
                it.setDimAmount(0f)
                it.setGravity(Gravity.CENTER)
            }
            mProgressDialog?.show()
        }
    }


    override fun hideProgressBar() {
        if (!isFinishing && !isDestroyed) {
            if (mProgressDialog != null && mProgressDialog?.isShowing == true) {
                mProgressDialog?.dismiss()
            }
            mProgressDialog = null
        }
    }


    override fun showShortToast(msg: String?) {
        msg?.let {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }


    override fun showLongToast(msg: String?) {
        msg?.let {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }


    override fun goBack() {
        onBackPressed()
    }


    override fun isNetworkAvailable(): Boolean {
        val connectivity =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo
        return (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected)
    }


    override fun showNoNetworkError() {
        showLongToast(NetworkStatusMessage.NO_INTERNET.message)
    }


    fun logout() {
        if (!isFinishing && !isDestroyed) {
            BaseApplication.instance.captureLogoutEvent(CustomEvents.LogoutEvent,viewModel?.getPhone()!!)

            val t = thread(start = false) {
                DataManager.clearAppData()
            }

            t.join()
            t.start()

            val intent = Intent(this, LoginSignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)



        }
    }


    fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = currentFocus
        if (view != null && (ev?.action == MotionEvent.ACTION_UP || ev?.action == MotionEvent.ACTION_MOVE)
            && view is EditText && !view.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)

            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]

            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(
                    window.decorView.applicationWindowToken, 0
                )
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun attachBaseContext(newBase: Context?) {

        var context1 = newBase

        val locale = Locale(DataManager.getAppLanguage())
        Locale.setDefault(locale)

        val res = newBase?.resources
        val config = Configuration(res?.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            context1 = newBase?.createConfigurationContext(config)

        } else {
            res?.updateConfiguration(config, res.displayMetrics)
        }

        super.attachBaseContext(context1)
    }


    /**
     * method to adjust FontScale/Density of the screen when changed manually from device settings
     */
    fun adjustFontScale(configuration: Configuration) {
        val displayMetrics = resources.displayMetrics
        var updateConfig = false

        if (configuration.fontScale > 1.0F) {
            configuration.fontScale = 1.0F
            updateConfig = true
        }
        if (displayMetrics.density > 2.625F) {
            displayMetrics.density = 2.625F
            displayMetrics.densityDpi = 420
            configuration.densityDpi = 420
            updateConfig = true
        }

        if (updateConfig) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                applyOverrideConfiguration(configuration)
//
//            } else {
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.scaledDensity = configuration.fontScale * displayMetrics.density
            resources.updateConfiguration(configuration, displayMetrics)
//            }
        }
    }


    override fun onDestroy() {
        hideProgressBar()
        super.onDestroy()
    }

}