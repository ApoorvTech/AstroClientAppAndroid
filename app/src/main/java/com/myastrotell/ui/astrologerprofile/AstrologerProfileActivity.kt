package com.myastrotell.ui.astrologerprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.chip.Chip
import com.myastrotell.R
import com.myastrotell.adapters.RatingReviewAdapter
import com.myastrotell.base.BaseActivity
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.ApiStatusCodes
import com.myastrotell.data.AppConstants
import com.myastrotell.data.AstrologersListType
import com.myastrotell.databinding.ActivityAstrologerProfileBinding
import com.myastrotell.dialogs.AppAlertDialog
import com.myastrotell.pojo.response.astrlogerprofile.ReviewModel
import com.myastrotell.ui.detailform.DetailsFormActivity
import com.myastrotell.ui.wallet.WalletActivity
import com.myastrotell.utils.*
import kotlinx.android.synthetic.main.activity_astrologer_profile.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar_primary.*
import kotlinx.android.synthetic.main.layout_wallet_recharge_header.*
import kotlinx.android.synthetic.main.list_item_daily_horoscope.view.*
import java.util.*
import kotlin.collections.ArrayList


class AstrologerProfileActivity :
    BaseActivity<ActivityAstrologerProfileBinding, AstrologerProfileViewModel>(),
    View.OnClickListener, TextToSpeech.OnInitListener {

    private lateinit var mReviewsAdapter: RatingReviewAdapter
    private lateinit var mReviewsList: ArrayList<ReviewModel>

    private var clickActionId: Int = 0

    private var isAstrologerOnline: Boolean = false

    private var refreshAstrologersList: Boolean = false
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        setUpAdapters()

        if (!isUserLoggedIn()) {
            rlWalletRecharge.gone()
        }

        viewModel?.getAstrologersDetails()
        tts = TextToSpeech(this, this)
        val speakout = speakout!!
        val speak = speak!!

    }


    override fun onResume() {
        super.onResume()

        atvBalance.text = getString(R.string.currency)
        atvBalance.append(getWalletBalance())

    }


    override fun initVariables() {
        mReviewsList = ArrayList()
    }


    override fun getLayoutId() = R.layout.activity_astrologer_profile


    override fun getBindingVariable() = 0


    override fun initViewModel() = getViewModel<AstrologerProfileViewModel>()


    private fun getData() {
        atvTitle.text = getString(R.string.profile)

        aivEnd.setImageResource(R.drawable.ic_share_white)
        llOptions.visibility = View.VISIBLE

        viewModel?.setIntent(intent)

        atvName.text = viewModel?.astrologerName

        sdvImage.setImageURI(viewModel?.astrologerImage)

        atvExperience.text = ("${intent?.getStringExtra(AppConstants.KEY_TIME)} Years")

        val avgRating = intent?.getFloatExtra(AppConstants.KEY_AVG_RATING, 0f) ?: 0f
        atvRating.text = String.format("%.2f", avgRating)

        ratingBar.rating = avgRating

        atvTotalPersonAttended.text =
            ("${intent?.getLongExtra(AppConstants.KEY_TOTAL_RATING, 0)} total")

    }


    override fun setObservers() {
        viewModel?.astrologerProfileWithRateLiveDate?.observe(this, Observer {
            hideProgressBar()

            llMain.visible()
            llBottomOptions.visible()

            it?.data?.let { dataList ->

                if (dataList.isNotEmpty()) {

                    dataList[0].let { data ->

                        atvLanguages.text = data.language

                        atvAbout.text = data.goodsDescription ?: ""

                        if (atvAbout.lineCount > 3) {
                            atvAbout.maxLines = 3
                            atvReadMore.isSelected = true

                        } else {
                            atvReadMore.gone()
                            atvAbout.maxLines = 50
                        }
                        speakout!!.setOnClickListener {
                            speakOut(atvAbout.text.toString());
                            speak!!.visible()
                            speakout!!.invisible()
                        }


                        speak!!.setOnClickListener {
                            speakout!!.visible()
                            speak!!.invisible()
                            tts!!.stop()

                        }
                        // setting skills
                        cgSkills.removeAllViews()
                        data.goodsShortDescription?.let { ss ->
                            if (ss.contains(",")) {
                                val skillsList = ss.split(",")

                                skillsList.forEach { skill ->
                                    val chip = LayoutInflater.from(this).inflate(
                                        R.layout.list_item_astrologer_skill, cgSkills, false
                                    ) as Chip
                                    chip.text = skill.trim()
                                    cgSkills.addView(chip)
                                }

                            } else {
                                val chip = Chip(this)
                                chip.text = ss.trim()
                                cgSkills.addView(chip)
                            }
                        }

                    }
                }

                dataList.forEach { data ->
                    when (data.goodsCategory) {
                        AstrologersListType.CHAT.value -> {
                            viewModel?.chatRate = data.goodsPrice ?: 0.0
                        }

                        AstrologersListType.CALL.value -> {
                            viewModel?.callRate = data.goodsPrice ?: 0.0
                        }

                        AstrologersListType.REPORT.value -> {
                            viewModel?.reportRate = data.goodsPrice ?: 0.0
                        }
                    }
                }

                atvChatRate.text =
                    String.format(getString(R.string.rate_format), viewModel?.chatRate)

                atvCallRate.text =
                    String.format(getString(R.string.rate_format), viewModel?.callRate)

            }

        })


        viewModel?.astrologersStatusListLiveDate?.observe(this, Observer {
            hideProgressBar()

            it.data?.forEach { model ->
                if (viewModel?.astrologerId.equals(model.productId, true)) {

                    if (model.productType.equals(AstrologersListType.CALL.value, true)) {
                        isAstrologerOnline = true

//                        if (!isAstrologerBusy)
                        viewModel?.isAvailableForCall = true

                    }

                    if (model.productType.equals(AstrologersListType.CHAT.value, true)) {
                        isAstrologerOnline = true

//                        if (!isAstrologerBusy)
                        viewModel?.isAvailableForChat = true

                    }
                }
            }

        })


        viewModel?.astrologersReviewListLiveDate?.observe(this, Observer {
            hideProgressBar()

            it?.let {
                it.data?.let { list ->
                    mReviewsList.addAll(list)
                    mReviewsAdapter.notifyDataSetChanged()
                }
            }

        })


        viewModel?.astrologersProductInfoListLiveDate?.observe(this, Observer {
            hideProgressBar()

            it?.data?.let { dataList ->
                for (data in dataList) {
                    when (data.category) {
                        AstrologersListType.CALL.value -> {
                            atvTotalCallTime.text = data.total.toString()

                        }
                        AstrologersListType.CHAT.value -> {
                            atvTotalChatTime.text = data.total.toString()

                        }
                        AstrologersListType.REPORT.value -> {
                            atvTotalReports.text = data.total.toString()

                        }
                    }
                }
            }

        })


        viewModel?.astrologerStatusLiveData?.observe(this, Observer {
            hideProgressBar()

            it?.data?.busy?.let { isBusy ->
                if (isBusy) {
                    AppAlertDialog(
                        this,
                        R.drawable.ic_alert,
                        getString(R.string.alert),
                        getString(R.string.astrologer_is_busy),
                        getString(R.string.ok),
                        "",
                        onPositiveBtnClicked = {
                            refreshAstrologersList = true
                        }
                    ).show()

                } else {

                    when(clickActionId){
                        R.id.rlChat ->{
                            handleChatButtonClick()
                        }

                        R.id.rlCall ->{
                            handleCallButtonClick()
                        }
                    }
                }
            }

        })


        viewModel?.callRequestLiveData?.observe(this, Observer {
            hideProgressBar()

            AppAlertDialog(
                this,
                R.drawable.ic_checked,
                getString(R.string.success),
                getString(R.string.your_call_request_has_been_submitted),
                getString(R.string.ok),
                null,
                onPositiveBtnClicked = {
                    refreshAstrologersList = true
                    onBackPressed()
                }
            ).show()

        })


        aivBack.setOnClickListener(this)
        aivEnd.setOnClickListener(this)
        atvRecharge.setOnClickListener(this)
        atvReadMore.setOnClickListener(this)
        rlChat.setOnClickListener(this)
        rlCall.setOnClickListener(this)

    }



    override fun handleApiErrorResponse(responseModel: BaseResponseModel<*>?) {
        if (responseModel?.code == ApiStatusCodes.NO_DATA_FOUND) {
            when (responseModel.apiRequestCode) {
                ApiRequestCodes.ASTROLOGER_REVIEW -> {

                }

                ApiRequestCodes.ASTROLOGER_TIME -> {

                }

                ApiRequestCodes.ALL_ASTROLOGERS_STATUS -> {

                }

                else -> {
                    super.handleApiErrorResponse(responseModel)
                }
            }

        } else {
            super.handleApiErrorResponse(responseModel)
        }
    }


    /**
     *  settings reviews adapter
     */
    private fun setUpAdapters() {
        mReviewsAdapter = RatingReviewAdapter(mReviewsList)
        rvReviews.itemAnimator = DefaultItemAnimator()
        rvReviews.adapter = mReviewsAdapter

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aivBack -> {
                onBackPressed()
            }

            R.id.atvReadMore -> {
                if (atvReadMore.isSelected) {
                    atvReadMore.isSelected = false
                    atvReadMore.text = getString(R.string.read_less)

                    atvAbout.maxLines = 60

                } else {
                    atvReadMore.isSelected = true
                    atvReadMore.text = getString(R.string.read_more)

                    atvAbout.maxLines = 3
                }
            }

            R.id.aivEnd -> { // Share Profile
                AppUtils.shareApplication(this, getString(R.string.app_name), getString(R.string.share_app_link_message))
                //AppUtils.shareTextWithIntentChooser(this, "", "Hey! Checkout this profile.")
            }

            R.id.atvRecharge -> {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }

            R.id.rlChat -> {
                if (isUserLoggedIn()) {

                    if (viewModel?.isUserBusy() == true) {
                        showUserBusyAlert()
                        return
                    }

                    if (isAstrologerOnline) {

                        if (viewModel?.isAvailableForChat == true) {

                            clickActionId = v.id

                            viewModel?.getAstrologerStatusByNumber(viewModel?.astrologerId.toString())

                        } else {
                            showShortToast(getString(R.string.astrologer_not_available_for_chat))
                        }

                    } else {
                        showShortToast(getString(R.string.astrologer_is_offline))
                    }

                } else {
                    showLoginDialog()
                }
            }

            R.id.rlCall -> {
                if (isUserLoggedIn()) {

                    if (viewModel?.isUserBusy() == true) {
                        showUserBusyAlert()
                        return
                    }

                    if (isAstrologerOnline) {

                        if (viewModel?.isAvailableForCall!!) {

                            clickActionId = v.id

                            viewModel?.getAstrologerStatusByNumber(viewModel?.astrologerId.toString())

                        } else {
                            showShortToast(getString(R.string.astrologer_not_available_for_call))
                        }

                    } else {
                        showShortToast(getString(R.string.astrologer_is_offline))
                    }

                } else {
                    showLoginDialog()
                }
            }

        }
    }


    /**
     * method to handle chat button click
     */
    private fun handleChatButtonClick() {
        if (!isFinishing && !isDestroyed) {
            val balance: Double = getWalletBalance().toDouble()
            val requiredBalance: Double = (viewModel?.chatRate ?: 0.00) * 5

            if (balance < requiredBalance) {
                showInsufficientBalanceAlert(
                    String.format(
                        getString(R.string.minimumBalanceForChat),
                        requiredBalance,
                        atvName.text
                    )
                )

            } else {

                openIntakeFormScreen(AstrologersListType.CHAT.value)

            }
        }
    }


    /**
     * method to handle call button click
     */
    private fun handleCallButtonClick() {
        if (!isFinishing && !isDestroyed) {
            val balance: Double = getWalletBalance().toDouble()
            val requiredBalance: Double = (viewModel?.callRate ?: 0.00) * 5

            if (balance < requiredBalance) {
                showInsufficientBalanceAlert(
                    String.format(
                        getString(R.string.minimumBalanceForCall),
                        requiredBalance,
                        atvName.text
                    )
                )
            } else {
                initCallRequest()
            }
        }
    }


    /**
     * method to open chat Intake form screen
     */
    private fun openIntakeFormScreen(type: String) {
        val intent = Intent(this, DetailsFormActivity::class.java)
        intent.putExtra(AppConstants.KEY_TYPE, type)
        intent.putExtra(AppConstants.KEY_ID, viewModel?.astrologerId)
        intent.putExtra(AppConstants.KEY_TITLE, viewModel?.astrologerName)
        intent.putExtra(AppConstants.KEY_IMAGE, viewModel?.astrologerImage)
        intent.putExtra(AppConstants.KEY_PRICE, viewModel?.callRate)
        intent.putExtra(AppConstants.KEY_LANGUAGES, atvLanguages.text)
        startActivityForResult(intent, AppConstants.DETAIL_FORM_REQUEST_CODE)
    }


    /**
     * method to initiate call request
     */
    private fun initCallRequest() {
        if (!isFinishing && !isDestroyed) {
            viewModel?.initCallRequest(
                viewModel?.astrologerId.toString(),
                viewModel?.callRate.toString(),
                getWalletBalance()
            )
        }
    }


    private fun showInsufficientBalanceAlert(message: String) {
        AppAlertDialog(
            this,
            R.drawable.ic_alert,
            getString(R.string.alert),
            message,
            getString(R.string.recharge),
            getString(R.string.cancel), onPositiveBtnClicked = {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }
        ).show()
    }


    private fun showUserBusyAlert() {
        AppAlertDialog(
            this,
            R.drawable.ic_alert,
            getString(R.string.alert),
            getString(R.string.you_cant_proceed_as_you_are_busy),
            getString(R.string.ok),
            null,
            onPositiveBtnClicked = {}
        ).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DETAIL_FORM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    refreshAstrologersList = true
                    viewModel?.isAvailableForChat = false
                    viewModel?.isAvailableForCall = false
                }
            }
        }
    }


    override fun onBackPressed() {
//        if (refreshAstrologersList)
        setResult(Activity.RESULT_OK)

        super.onBackPressed()
    }


    override fun showProgressBar() {
        progressBar?.visible()
    }


    override fun hideProgressBar() {
        progressBar?.gone()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale("en", "IN"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
    private fun speakOut(status: String ) {
        val text = status
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)

    }

}
