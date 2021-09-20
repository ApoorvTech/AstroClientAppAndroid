package com.myastrotell.data.restapi

import com.myastrotell.BuildConfig
import com.myastrotell.base.BaseResponseModel
import com.myastrotell.data.ApiEndPoints
import com.myastrotell.pojo.requests.*
import com.myastrotell.pojo.requests.buymallproduct.BuyProductRequest
import com.myastrotell.pojo.requests.buymallproduct.ValidateMallVoucherRequest
import com.myastrotell.pojo.requests.payment.InitiatePaymentModel
import com.myastrotell.pojo.requests.payment.InitiatePaymentResponse
import com.myastrotell.pojo.response.*
import com.myastrotell.pojo.response.astrlogerprofile.AstrologerProfileData
import com.myastrotell.pojo.response.astrlogerprofile.AstrologerProfileWithRateResponse
import com.myastrotell.pojo.response.astrlogerprofile.ItemInfo
import com.myastrotell.pojo.response.astrlogerprofile.ReviewModel
import com.myastrotell.pojo.response.userchatstatus.ChatStatusResposne
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiInterface {


    @POST(ApiEndPoints.CHECK_PROFILE_STATUS)
    suspend fun checkProfileStatus(@Body request: CheckNumberRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.SEND_OTP)
    suspend fun sendOTP(@Body request: SendOTP): BaseResponseModel<Any>


    @POST(ApiEndPoints.VALIDATE_OTP)
    suspend fun validateOTP(@Body request: ValidateOTP): BaseResponseModel<ValidateOtpResponse>


    @POST(ApiEndPoints.RESET_PASSWORD_SEND_OTP)
    suspend fun sendOtpForResetPassword(@Body request: SendOtpForResetPassword): BaseResponseModel<Any>


    @POST(ApiEndPoints.RESET_PASSWORD_VALIDATE_OTP)
    suspend fun validateOtpForResetPassword(@Body request: ValidateOTPForResetPassword): BaseResponseModel<ValidateOtpResponse>


    @POST(ApiEndPoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): BaseResponseModel<LoginResponse>


    @POST(ApiEndPoints.USER_PROFILE_DETAILS)
    suspend fun createUserProfile(@Body request: UserProfileDetailRequest): BaseResponseModel<ArrayList<UserProfileDetailRequest>>


    @POST(ApiEndPoints.UPLOAD_FILE)
    suspend fun uploadProfileImage(@Body requestBody: RequestBody): BaseResponseModel<UploadFileResponse>


    @POST(ApiEndPoints.USER_PROFILE_DETAILS)
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): BaseResponseModel<ArrayList<ProfileDetailData>>


    @GET(ApiEndPoints.USER_PROFILE_DETAILS)
    suspend fun getUserProfileDetails(): BaseResponseModel<UserProfileResponse>


    @POST(ApiEndPoints.USER_PROFILE_DETAILS)
    suspend fun resetPassword(@Body request: UserProfileDetailRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.HOME_FEATURES_LIST)
    suspend fun getHomeData(): BaseResponseModel<HomeDataResponse>


    @GET(ApiEndPoints.HOME_STATIC_DATA)
    suspend fun getHomeStaticData(): BaseResponseModel<List<HomeStaticDataResponse>>


    @POST(ApiEndPoints.HOROSCOPE)
    suspend fun getHoroscope(@Body request: HoroscopeRequest): BaseResponseModel<HororsopeResponse>

    @POST(ApiEndPoints.SEARCHBYCATEGGORY)
    suspend fun getSearchByCategory(@Body request: SearchByCategoryRequest): BaseResponseModel<SearchByCategoryResponse>


    @POST(ApiEndPoints.REDEMPTION_SEARCH)
    suspend fun getRedemptionSearchItems(@Body request: RedemptionSearchRequest): BaseResponseModel<List<RedemptionSearchResponse>>


    @POST(ApiEndPoints.PRODUCT_BILLING_DETAILS)
    suspend fun getProductBillingList(@Body request: ProductBillingRequest): BaseResponseModel<List<ProductBillingDetails>>


    @POST(ApiEndPoints.PRODUCT_BILLING)
    suspend fun getProductBillingDetails(@Body request: ProductBillingDetailRequest): BaseResponseModel<OrderDetailResponse>


    @POST(ApiEndPoints.NOTIFICATIONS)
    suspend fun getNotifications(): BaseResponseModel<List<NotificationResponse>>


    @POST(ApiEndPoints.IN_APP_NOTIFICATION_LIST)
    suspend fun getNotificationsList(@Body request: InAppNotificationReq): BaseResponseModel<NotificationData>


    @POST(ApiEndPoints.ASTROLOGERS_LIST)
    suspend fun getAstrologersList(@Body request: RedemptionSearchRequest): BaseResponseModel<ArrayList<AstrologerListResponse>>


    @POST(ApiEndPoints.WALLET_BALANCE)
    suspend fun getWalletBalance(): BaseResponseModel<WalletBalanceResponse>


    @GET(ApiEndPoints.OFFER_LIST)
    suspend fun getOfferList(): BaseResponseModel<List<OfferListResponse>>


    @POST(ApiEndPoints.GET_ADDRESS_LIST)
    suspend fun getAddressList(@Body request: GetAddressRequest): BaseResponseModel<List<AddressListResponse>>


    @POST(ApiEndPoints.SAVE_ADDRESS)
    suspend fun saveAddress(@Body request: AddEditAddressRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.EDIT_ADDRESS)
    suspend fun editAddress(@Body request: AddEditAddressRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.DELETE_ADDRESS)
    suspend fun deleteAddress(@Body request: DeleteAddressRequest): BaseResponseModel<Any>

    @POST(ApiEndPoints.GET_KEY_VALUE)
    suspend fun getKeyValues(@Body request: KeyDataRequest): BaseResponseModel<List<KeyDataResponse>>


    @GET(ApiEndPoints.USER_PROFILE_DETAILS + "/{msisdn}")
    suspend fun getAstrologerDetails(@Path("msisdn") msisdn: String): BaseResponseModel<AstrologerProfileData>


    @POST(ApiEndPoints.ASTROLOGER_DETAILS_WITH_RATE)
    suspend fun getAstrologerDetailsWithRate(@Body request: RedemptionSearchRequest): BaseResponseModel<List<AstrologerProfileWithRateResponse>>


    @POST(ApiEndPoints.PROFILE_PRODUCT_DETAILS)
    suspend fun getProductInfo(@Body request: ProductInfoRequest): BaseResponseModel<List<ItemInfo>>


    @POST(ApiEndPoints.REVIEW)
    suspend fun getReviewList(@Body request: ReviewRequest): BaseResponseModel<List<ReviewModel>>


    @POST(ApiEndPoints.SUBMIT_REVIEW)
    suspend fun submitReview(@Body request: SubmitReviewRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.SAVE_INTAKE_FORM)
    suspend fun saveIntakeForm(@Body request: IntakeFormRequest): BaseResponseModel<List<IntakeFormFieldModel>>


    // UnUsed
    @FormUrlEncoded
    @POST(ApiEndPoints.PRE_START_CHAT)
    suspend fun sendChatRequest(@FieldMap data: HashMap<String, String>): BaseResponseModel<StartChatResponse>

    // UnUsed
    @FormUrlEncoded
    @POST(ApiEndPoints.START_CHAT)
    suspend fun startChat(@FieldMap data: HashMap<String, String>): BaseResponseModel<StartChatResponse>


    @FormUrlEncoded
    @POST(ApiEndPoints.CONVERSATION_START_V2)
    suspend fun startSocketChat(@FieldMap data: HashMap<String, String>): BaseResponseModel<StartChatResponse>


    @FormUrlEncoded
    @POST(ApiEndPoints.END_CHAT)
    suspend fun endChat(@FieldMap data: HashMap<String, String>): BaseResponseModel<Any>


    // UnUsed
    @FormUrlEncoded
    @POST(ApiEndPoints.SEND_CHAT)
    suspend fun sendMessage(@FieldMap data: HashMap<String, String>): BaseResponseModel<Any>


    // UnUsed
    @FormUrlEncoded
    @POST(ApiEndPoints.NEW_CHATS)
    suspend fun newChats(@FieldMap data: HashMap<String, String>): BaseResponseModel<List<MessageResponse>>


    @FormUrlEncoded
    @POST(ApiEndPoints.GET_CHAT_HISTORY)
    suspend fun getChatHistory(@FieldMap data: HashMap<String, String>): BaseResponseModel<List<MessageResponse>>


    @FormUrlEncoded
    @POST(ApiEndPoints.GET_BUSY_ASTROLOGERS)
    suspend fun getBusyAstrologersList(@Field("appName") appName: String): BaseResponseModel<List<String>>


    @FormUrlEncoded
    @POST(ApiEndPoints.GET_BUSY_ASTROLOGERS)
    suspend fun getAstrologerStatusByNumber(
        @Field("appName") appName: String = BuildConfig.APP_NAME,
        @Field("msisdn") astrologerMsisdn: String
    ): BaseResponseModel<AstrologerStatusByMsisdnResponse>


    @POST(ApiEndPoints.GET_ASTROLOGERS_STATUS)
    suspend fun getAllAstrologersStatus(): BaseResponseModel<List<AstrologersStatusResponse>>


    @FormUrlEncoded
    @POST(ApiEndPoints.GET_LIVE_PANDIT_FOR_USER)
    suspend fun getChatStatusForUser(@FieldMap data: HashMap<String, String>): BaseResponseModel<List<ChatStatusResposne>>


    @POST(ApiEndPoints.GET_REPORT_STATUS_FOR_USER)
    suspend fun getReportStatusForUser(@Body request: RedemptionSearchRequest): BaseResponseModel<List<OrderDetailResponse>>


    @POST(ApiEndPoints.BUY_PRODUCT)
    suspend fun buyProduct(@Body request: BuyProductRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.WALLET_PASSBOOK)
    suspend fun getWalletPassbook(@Body request: WalletPassbookRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.WALLET_TRANSACTIONS)
    suspend fun getWalletTransactions(@Body request: WalletPassbookRequest): BaseResponseModel<List<WalletTransactionResponse>>


    @POST(ApiEndPoints.REDEEM_POINTS)
    suspend fun reedeemPoints(@Body request: RedeemPointsRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.CALL_REQUEST)
    suspend fun callRequest(@Body request: CallRequest): BaseResponseModel<CallRequestResponse>


    @POST(ApiEndPoints.SUBSCRIBE_FOR_ONLINE_STATUS)
    suspend fun subscribeForOnlineStatus(@Body request: SubscribeForOnlineStatusRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.SUBSCRIBE_FOR_AVAILABLE_STATUS)
    suspend fun subscribeForAvailableStatus(@Body request: SubscribeForAvailableStatusRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.AUTHENTICATE_PROMO_CODE)
    suspend fun authenticateVoucher(@Body request: AuthenticateVoucherRequest): BaseResponseModel<AuthenticateVoucherResponse>


    @POST(ApiEndPoints.AUTHENTICATE_MALL_PROMO_CODE)
    suspend fun authenticateMallVoucher(@Body request: ValidateMallVoucherRequest): BaseResponseModel<ValidateMallVoucherResponse>


    @GET(ApiEndPoints.PAYMENT_SUMMARY)
    suspend fun getPaymentSummary(@Query("amount") amount: String): BaseResponseModel<OfferListResponse>


    @POST(ApiEndPoints.REGISTER_FCM_ID)
    suspend fun registerFcmId(@Body request: RegisterFcmIdRequest): BaseResponseModel<Any>


    @GET(ApiEndPoints.PRIVACY_POLICY)
    suspend fun getTermsAndPolicy(): BaseResponseModel<TermsAndPolicyResponse>


    @POST(ApiEndPoints.FEEDBACK)
    suspend fun submitSupportForm(@Body request: SubmitSupportFormRequest): BaseResponseModel<Any>


    @POST(ApiEndPoints.APP_UPDATE)
    suspend fun appUpdate(@Body request: AppUpdateRequest): BaseResponseModel<AppUpdateResponse>



    @GET(ApiEndPoints.GET_FIRST_RECHARGE + "/{msisdn}")
    suspend fun getRechargeDetails(@Path("msisdn") msisdn: String): BaseResponseModel<Boolean>



    //payment

    @FormUrlEncoded
    @POST(ApiEndPoints.GET_RSA_KEY)
    suspend fun getPublicKey(@FieldMap data: HashMap<String, String>): String


    @POST(BuildConfig.INITIATE_PAYMENT)
    suspend fun initiatePayment(@Body data: InitiatePaymentModel): BaseResponseModel<InitiatePaymentResponse>





}
