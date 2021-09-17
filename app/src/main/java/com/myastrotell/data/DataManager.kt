package com.myastrotell.data

import com.myastrotell.BaseApplication
import com.myastrotell.data.database.AppDatabase
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.data.database.entities.UserProfileEntity
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.data.restapi.ApiInterface
import com.myastrotell.data.restapi.ApiManager
import com.myastrotell.pojo.DetailFormSelectionModel
import com.myastrotell.pojo.requests.*
import com.myastrotell.pojo.requests.buymallproduct.BuyProductRequest
import com.myastrotell.pojo.requests.buymallproduct.ValidateMallVoucherRequest
import com.myastrotell.pojo.requests.payment.InitiatePaymentModel
import com.myastrotell.pojo.response.KeyDataResponse
import com.myastrotell.pojo.response.UpdateProfileRequest
import okhttp3.RequestBody


/**
 * Handle all data related task here.
 */

object DataManager {

    private var apiService: ApiInterface
    private var chatMachineApiService: ApiInterface
    private var notificationApiService: ApiInterface
    private var paymentApiService: ApiInterface
    private var appDatabase: AppDatabase

    init {
        apiService = ApiManager.retrofitService
        notificationApiService = ApiManager.notificationRetrofitService
        paymentApiService = ApiManager.paymentRetrofitService
        chatMachineApiService = ApiManager.chatMachineRetrofitService
        appDatabase = AppDatabase.getInstance(BaseApplication.instance)
    }


    fun getStringFromPreference(key: String): String {
        return PreferenceManager.getString(key)
    }

    fun getIntFromPreference(key: String): Int {
        return PreferenceManager.getInt(key)
    }

    fun getBooleanFromPreference(key: String): Boolean {
        return PreferenceManager.getBoolean(key)
    }


    fun saveStringInPreference(key: String, value: String) {
        return PreferenceManager.saveString(key, value)
    }

    fun saveIntInPreference(key: String, value: Int) {
        return PreferenceManager.saveInt(key, value)
    }

    fun saveBooleanInPreference(key: String, value: Boolean) {
        return PreferenceManager.saveBoolean(key, value)
    }


    fun isLoggedIn(): Boolean {
        return PreferenceManager.getBoolean(PreferenceManager.IS_LOGGED_IN)
    }

    fun isGuestUser(): Boolean {
        return PreferenceManager.getBoolean(PreferenceManager.IS_GUEST_USER)
    }

    fun getAppLanguage(): String {
        return PreferenceManager.getString(PreferenceManager.SELECTED_LOCALE)
    }

    fun clearAppData() {
        PreferenceManager.clearAllPref()
        appDatabase.clearAllTables()
        setMsisdn("0")
    }


    fun setMsisdn(msisdn: String) {
        PreferenceManager.saveString(PreferenceManager.MSISDN, msisdn)
    }


    fun getMsisdn(): String {
        return PreferenceManager.getString(PreferenceManager.MSISDN)
    }


    /**
     * ========================
     *  Database Query Methods
     * ========================
     */

    suspend fun getCartData(): List<CartEntity> {
        return appDatabase.cartDao().getCartData()
    }

    suspend fun getCartItemByProductId(productId: String): CartEntity? {
        return appDatabase.cartDao().getItemById(productId)
    }

    suspend fun getCartItemsCount(): Int {
        return appDatabase.cartDao().getTotalItemsCount()
    }

    suspend fun addItemInCart(entity: CartEntity) {
        appDatabase.cartDao().insertIntoCart(entity)
    }

    suspend fun updateQuantity(productId: String, quantity: Int) {
        appDatabase.cartDao().updateQuantity(productId, quantity)
    }

    suspend fun updateConsultantName(productId: String, consultantName: String) {
        appDatabase.cartDao().updateConsultantName(productId, consultantName)
    }

    suspend fun removeItemFromCart(productId: String) {
        appDatabase.cartDao().deleteItemFromCart(productId)
    }

    suspend fun clearCart() {
        appDatabase.cartDao().clearCart()
    }


    suspend fun getIntakeFormData(): IntakeFormEntity? {
        return appDatabase.intakeFormDao().getIntakeFormData(getMsisdn())
    }

    suspend fun addIntakeForm(data: IntakeFormEntity) {
        appDatabase.intakeFormDao().insertIntoIntakeForm(data)
    }

    suspend fun clearIntakeFormData() {
        appDatabase.intakeFormDao().clearIntakeFormData()
    }


    suspend fun getProfileData(): UserProfileEntity? {
        return appDatabase.userProfileDao().getProfileData(getMsisdn())
    }

    suspend fun saveProfileData(data: UserProfileEntity) {
        appDatabase.userProfileDao().insertProfileData(data)
    }


    suspend fun getSelectedLanguages(): List<SelectedLanguagesEntity> {
        return appDatabase.selectedLanguageDao().getSelectedLanguages()
    }

    suspend fun addLanguage(entity: SelectedLanguagesEntity) {
        appDatabase.selectedLanguageDao().insertLanguage(entity)
    }

    suspend fun clearLanguages() {
        appDatabase.selectedLanguageDao().clearLanguages()
    }


    /**
     * ==================
     *  API Call Methods
     * ==================
     */

    suspend fun checkProfileStatus(request: CheckNumberRequest) =
        apiService.checkProfileStatus(request)


    suspend fun sendOTP(request: SendOTP) = apiService.sendOTP(request)


    suspend fun validateOTP(request: ValidateOTP) = apiService.validateOTP(request)


    suspend fun sendOtpForResetPassword(request: SendOtpForResetPassword) =
        apiService.sendOtpForResetPassword(request)


    suspend fun validateOtpForResetPassword(request: ValidateOTPForResetPassword) =
        apiService.validateOtpForResetPassword(request)


    suspend fun login(request: LoginRequest) = apiService.login(request)


    suspend fun createUserProfile(request: UserProfileDetailRequest) =
        apiService.createUserProfile(request)


    suspend fun uploadProfileImage(requestBody: RequestBody) =
        apiService.uploadProfileImage(requestBody)


    suspend fun updateUserProfile(request: UpdateProfileRequest) =
        apiService.updateUserProfile(request)


    suspend fun getUserProfileDetails() = apiService.getUserProfileDetails()


    suspend fun resetPassword(request: UserProfileDetailRequest) =
        apiService.resetPassword(request)


    suspend fun getHomeData() = apiService.getHomeData()


    suspend fun getHomeStaticData() = apiService.getHomeStaticData()


    suspend fun getHoroscope(request: HoroscopeRequest) = apiService.getHoroscope(request)


    suspend fun getSearchByCategory(request: SearchByCategoryRequest) =
        apiService.getSearchByCategory(request)


    suspend fun getAstrologersList(request: RedemptionSearchRequest) =
        apiService.getAstrologersList(request)


    suspend fun getWalletBalance() = apiService.getWalletBalance()


    suspend fun getOfferList() = apiService.getOfferList()


    suspend fun getRedemptionSearchItems(request: RedemptionSearchRequest) =
        apiService.getRedemptionSearchItems(request)


    suspend fun getProductBillingList(request: ProductBillingRequest) =
        apiService.getProductBillingList(request)


    suspend fun getProductBillingDetails(request: ProductBillingDetailRequest) =
        apiService.getProductBillingDetails(request)


    suspend fun getNotifications() = apiService.getNotifications()

    suspend fun getNotificationsList(request: InAppNotificationReq) =
        apiService.getNotificationsList(request)


    suspend fun getAddressList(request: GetAddressRequest) = apiService.getAddressList(request)


    suspend fun saveAddress(request: AddEditAddressRequest) = apiService.saveAddress(request)


    suspend fun editAddress(request: AddEditAddressRequest) = apiService.editAddress(request)


    suspend fun deleteAddress(request: DeleteAddressRequest) = apiService.deleteAddress(request)


    //astrologers profile details
    suspend fun getAstrologerDetails(msisdn: String) = apiService.getAstrologerDetails(msisdn)

    suspend fun getAstrologerDetailsWithRate(request: RedemptionSearchRequest) =
        apiService.getAstrologerDetailsWithRate(request)

    suspend fun getProductInfo(request: ProductInfoRequest) = apiService.getProductInfo(request)

    suspend fun getReviewList(request: ReviewRequest) = apiService.getReviewList(request)

    suspend fun submitReview(request: SubmitReviewRequest) = apiService.submitReview(request)


    //buy product
    suspend fun buyProduct(request: BuyProductRequest) = apiService.buyProduct(request)


    suspend fun getWalletPassbook(request: WalletPassbookRequest) =
        apiService.getWalletPassbook(request)

    suspend fun getWalletTransactions(request: WalletPassbookRequest) =
        apiService.getWalletTransactions(request)


    suspend fun redeemPoints(request: RedeemPointsRequest) = apiService.reedeemPoints(request)

    suspend fun getKeyValue(request: KeyDataRequest) = apiService.getKeyValues(request)

    suspend fun saveIntakeForm(request: IntakeFormRequest) = apiService.saveIntakeForm(request)

    suspend fun callRequest(request: CallRequest) = apiService.callRequest(request)


    // UnUsed
    suspend fun sendChatRequest(data: HashMap<String, String>) = apiService.sendChatRequest(data)

    // UnUsed
    suspend fun startChat(data: HashMap<String, String>) = apiService.startChat(data)

    // UnUsed
    suspend fun endChat(data: HashMap<String, String>) = apiService.endChat(data)

    // UnUsed
    suspend fun sendMessage(data: HashMap<String, String>) = apiService.sendMessage(data)

    // UnUsed
    suspend fun newChats(data: HashMap<String, String>) = apiService.newChats(data)


    suspend fun getChatHistory(data: HashMap<String, String>) = chatMachineApiService.getChatHistory(data)

    suspend fun startSocketChat(data: HashMap<String, String>) = chatMachineApiService.startSocketChat(data)

    suspend fun endSocketChat(data: HashMap<String, String>) = chatMachineApiService.endChat(data)


    suspend fun getBusyAstrologersList(appName: String) =
        chatMachineApiService.getBusyAstrologersList(appName)

    suspend fun getAstrologerStatusByNumber(astrologerMsisdn: String) =
        chatMachineApiService.getAstrologerStatusByNumber(astrologerMsisdn = astrologerMsisdn)

    suspend fun getAllAstrologersStatus() = apiService.getAllAstrologersStatus()

    suspend fun getChatStatusForUser(params: HashMap<String, String>) =
        chatMachineApiService.getChatStatusForUser(params)

    suspend fun getReportStatusForUser(request: RedemptionSearchRequest) =
        apiService.getReportStatusForUser(request)


    suspend fun getTermsAndPolicy() = apiService.getTermsAndPolicy()


    suspend fun subscribeForOnlineStatus(request: SubscribeForOnlineStatusRequest) =
        notificationApiService.subscribeForOnlineStatus(request)


    suspend fun subscribeForAvailableStatus(request: SubscribeForAvailableStatusRequest) =
        chatMachineApiService.subscribeForAvailableStatus(request)


    suspend fun authenticateVoucher(request: AuthenticateVoucherRequest) =
        notificationApiService.authenticateVoucher(request)


    suspend fun authenticateMallVoucher(request: ValidateMallVoucherRequest) =
        notificationApiService.authenticateMallVoucher(request)


    suspend fun getPaymentSummary(amount: String) = notificationApiService.getPaymentSummary(amount)

    suspend fun registerFcmId(request: RegisterFcmIdRequest) =
        notificationApiService.registerFcmId(request)


    suspend fun submitSupportForm(request: SubmitSupportFormRequest) =
        notificationApiService.submitSupportForm(request)

    suspend fun getRechargeDetails(msisdn: String) = apiService.getRechargeDetails(msisdn)


    suspend fun appUpdate(request: AppUpdateRequest = AppUpdateRequest()) = apiService.appUpdate(request)



    //payment
    suspend fun getRsaKey(data: HashMap<String, String>) = paymentApiService.getPublicKey(data)

    suspend fun initiatePayment(data: InitiatePaymentModel) =
        paymentApiService.initiatePayment(data)


    /**
     * ==================
     *  Detail form concerns
     * ==================
     */

    var detailsFormConcerns: List<DetailFormSelectionModel>? = null

    var languageList: List<KeyDataResponse>? = null

}