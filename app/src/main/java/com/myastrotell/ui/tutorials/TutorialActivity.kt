package com.myastrotell.ui.tutorials

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.myastrotell.R
import com.myastrotell.adapters.TutorialPagerAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.data.AppConstants
import com.myastrotell.data.DataManager
import com.myastrotell.databinding.ActivityTutorialBinding
import com.myastrotell.ui.home.HomeActivity
import com.myastrotell.ui.login.LoginSignUpActivity
import com.myastrotell.ui.login.LoginSignUpViewModel
import com.myastrotell.ui.termsandpolicy.TermsAndPolicyActivity
import com.myastrotell.utils.getViewModel
import kotlinx.android.synthetic.main.activity_tutorial.*


class TutorialActivity : BaseActivity<ActivityTutorialBinding, LoginSignUpViewModel>(),
    View.OnClickListener {

    private lateinit var adapter: TutorialPagerAdapter
    private var datalist =
        listOf(R.drawable.ic_tutorial_1, R.drawable.ic_tutorial_2, R.drawable.ic_tutorial_3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpAdapters()

        setListeners()

        adapter.notifyDataSetChanged()

        tutorialIndicators.setViewPager(tutorialPager)

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val termsOfUse = SpannableString(getString(R.string.terms_of_use))
        termsOfUse.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(this@TutorialActivity, TermsAndPolicyActivity::class.java)
                    intent.putExtra(AppConstants.KEY_DATA, getString(R.string.terms_of_use))
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@TutorialActivity, R.color.colorBlue)
                }

            }, 0, termsOfUse.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        val privacyPolicy = SpannableString(getString(R.string.privacy_policy))
        privacyPolicy.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(this@TutorialActivity, TermsAndPolicyActivity::class.java)
                    intent.putExtra(AppConstants.KEY_DATA, getString(R.string.privacy_policy))
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@TutorialActivity, R.color.colorBlue)
                }

            }, 0, privacyPolicy.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        termsAndPolicyTV.movementMethod = LinkMovementMethod.getInstance()
        termsAndPolicyTV.highlightColor = Color.TRANSPARENT

        termsAndPolicyTV.text = getString(R.string.by_signing_up_you_agree_to_our)
        termsAndPolicyTV.append(" ")
        termsAndPolicyTV.append(termsOfUse)
        termsAndPolicyTV.append(" ")
        termsAndPolicyTV.append(getString(R.string._and_))
        termsAndPolicyTV.append(" ")
        termsAndPolicyTV.append(privacyPolicy)

    }

    override fun getLayoutId() = R.layout.activity_tutorial

    override fun getBindingVariable() = 0

    override fun initViewModel() = getViewModel<LoginSignUpViewModel>()

    override fun initVariables() {

    }

    override fun setObservers() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginSignupView -> {
                viewModel?.setTutorialsShown(true)

                val intent = Intent(this, LoginSignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            R.id.atvSkipTutorials -> {
                // updating details for guest login
                viewModel?.setGuestDetails()

                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

    }

    /**
     * method to set up Adapters
     */
    private fun setUpAdapters() {
        adapter = TutorialPagerAdapter(datalist)
        tutorialPager.adapter = adapter
    }

    private fun setListeners() {
        loginSignupView.setOnClickListener(this)
        atvSkipTutorials.setOnClickListener(this)
    }


}