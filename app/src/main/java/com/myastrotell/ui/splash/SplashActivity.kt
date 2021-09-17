package com.myastrotell.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.myastrotell.R
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppUpdateType
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.databinding.ActivitySplashBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.ui.home.HomeActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.tutorials.TutorialActivity
import com.myastrotell.utils.AppUtils
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    private var appUpdateDialog: AppAlertDialog? = null
    private var updateType: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startAnimation()

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (DataManager.getMsisdn().isBlank()) {
            DataManager.setMsisdn("0")
        }

        viewModel?.getAppUpdate()

        DataManager.saveStringInPreference(PreferenceManager.DEVICE_ID, AppUtils.getDeviceId(this))

    }


    override fun getLayoutId() = R.layout.activity_splash


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<SplashViewModel>()


    override fun initVariables() {

    }


    override fun onStart() {
        super.onStart()
        handleAppUpdateDialog()
    }


    override fun setObservers() {
        viewModel?.appUpdateLiveData?.observe(this, Observer {
            hideProgressBar()
            updateType = it?.data?.updateType ?: "0"
            handleAppUpdateDialog()
        })
    }


    private fun handleAppUpdateDialog() {
        when (updateType) {
            AppUpdateType.FORCE_UPDATE.value -> {
                if (appUpdateDialog == null) {
                    appUpdateDialog = AppAlertDialog(this,
                        R.drawable.ic_alert,
                        getString(R.string.app_update),
                        getString(R.string.app_update_message),
                        getString(R.string.update_now),
                        null,
                        onPositiveBtnClicked = {
                            appUpdateDialog = null
                            AppUtils.openAppInGooglePlayStore(this)
                        }
                    )
                    appUpdateDialog?.show()
                }
            }

            AppUpdateType.SOFT_UPDATE.value -> {
                if (appUpdateDialog == null) {
                    appUpdateDialog = AppAlertDialog(this,
                        R.drawable.ic_alert,
                        getString(R.string.app_update),
                        getString(R.string.app_update_message),
                        getString(R.string.update),
                        getString(R.string.txt_not_now),
                        onPositiveBtnClicked = {
                            appUpdateDialog = null
                            AppUtils.openAppInGooglePlayStore(this)
                        },
                        onNegativeBtnClicked = {
                            appUpdateDialog = null
                            navigateToNextScreen()
                        }
                    )
                    appUpdateDialog?.show()
                }
            }

            AppUpdateType.NO_UPDATE.value -> {
                navigateToNextScreen()
            }
        }
    }


    private fun navigateToNextScreen() {
        Handler().postDelayed(Runnable {

            if (isUserLoggedIn() || isGuestUser()) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))

            } else if (!DataManager.getBooleanFromPreference(PreferenceManager.ARE_TUTORIALS_SHOWN)) {
                startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))

            } else {
                startActivity(Intent(this@SplashActivity, LoginSignUpActivity::class.java))
            }
            finish()

        }, 3000)
    }


    private fun startAnimation() {
        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)
        ivSplash.startAnimation(rotation)
    }


    private fun stopAnimation() {
        ivSplash.clearAnimation()
    }


    override fun onDestroy() {
        stopAnimation()
        super.onDestroy()
    }

}