package com.myastrotell.data


/**
 * All app related constants are defined in this file
 */
object AppConstants {

    const val CURRENCY: String = "INR"
    const val DUMMY_IMAGE = "https://www.adeptfluidyne.com/wp-content/uploads/2017/08/banner.jpg"
    const val DUMMY_ROUND_IMAGE = "https://www.medispera.com/wp-content/uploads/2014/04/circle.png"


    const val KEY_ID = "id"
    const val KEY_DATE = "date"
    const val KEY_NUMBER = "number"
    const val KEY_CHAT_ID = "chat_id"
    const val KEY_NAME = "name"
    const val KEY_AMOUNT = "amount"
    const val KEY_TOTAL_AMOUNT = "totalAmount"
    const val KEY_VOUCHER_TRANSACTION_ID = "voucherTransactionId"
    const val KEY_IMAGE = "Image"
    const val KEY_PRICE = "price"
    const val KEY_DATA = "data"
    const val KEY_FROM = "from"
    const val KEY_TYPE = "type"
    const val KEY_POSITION = "position"
    const val KEY_SUN_SIGN = "sun_sign"
    const val KEY_LANGUAGES = "languages"
    const val KEY_AVG_RATING = "avg rating"
    const val KEY_TOTAL_RATING = "total rating"
    const val KEY_TIME = "time"
    const val KEY_IS_SHOWING_HISTORY = "is_showing_history"
    const val KEY_IS_INITIATING_CHAT = "is_initiating_chat"
    const val KEY_IS_BUSY = "is_busy"
    const val KEY_VOUCHER_DATA = "voucherData"
    const val KEY_CASHBACK_AMOUNT = "cashbackAmount"
    const val KEY_TITLE = "title"
    const val KEY_MESSAGE = "message"
    const val KEY_TARGET_ACTION = "targetAction"
    const val KEY_CLICK_ACTION = "click_action"
    const val KEY_TARGET_ACTION_DATA = "targetActionData"


    const val FCM_FILTER_NAME = "FCM_FILTER"


    const val DETAIL_FORM_REQUEST_CODE = 1002
    const val ASTROLOGER_PROFILE_REQUEST_CODE = 1003
    const val PAYMENT_INFO_REQUEST_CODE = 1004
    const val INIT_PAYMENT_REQUEST_CODE = 1005
    const val EDIT_PROFILE_REQUEST_CODE = 1006
    const val ADD_EDIT_ADDRESS_REQUEST_CODE = 1007

    const val RC_CAMERA_PERMISSION = 101
    const val RC_STORAGE_PERMISSION = 102

}

object CapturedEvents {
    const val BUSY: String = "Busy"
    const val PLATFORM: String = "Android"
    const val LOGIN_PLATFORM: String = "Login Platform"
    const val SUCCES: String = "Success"
    const val FAIL: String = "Fail"
    const val UNKNOWN: String = "Unknown"
    const val CANCEL: String = "Cancel by user"
    const val AMOUNT: String = "Amount"
    const val LOGIN_STATUS: String = "Login Status"
    const val TAB_CLICKED: String = "Tab Name "
    const val LOGIN_YES: String = "Yes "
    const val LOGIN_NO: String = "No "
    const val NO_MOB: String = "-"
    const val STATUS: String = "Status"
    const val INITIATED: String = "Initiated"
    const val SUBMITED: String = "Submitted"
    const val QUEUE: String = "InQueue"
    const val PHONE: String = "Phone Number"
    const val REVENUE: String = "Revenue"
    const val EMAIL: String = "Email"
    const val DATE: String = "DateTime"
    const val NAME: String = "Name"
    const val ASTRO_NAME: String = "Astrologer Name"
    const val EVENT: String = "Event"

    object TRANSACTION_STATUS {
        const val SUCCESS: Int = 1
        const val FAIL: Int = 0
        const val CANCEL_BY_USER: Int = -1
    }
}

object CustomEvents {
    const val OrderHistory: String = "LWgUpDOhq7"
    const val ShareEvent: String = "dxZXGG1qqL"
    const val SignUpEvent: String = "jNKtgLUGGo"
    const val InitiateCallEvent: String = "qbxMx5v8XW"
    const val InitiateChatEvent: String = "yEX9tBhZRh"
    const val InitiateReportEvent: String = "sco9CpslYC"
    const val LogoutEvent: String = "oISpiQdGsU"
    const val MallPurchase: String = "fspbHAZG3i"
    const val Recharge50: String = "pIE3bOtrEJ"
    const val Recharge100: String = "vW5HV7EBaI"
    const val Recharge200: String = "XqyHHKLRzW"
    const val Recharge300: String = "_5Gm-scUiF"
    const val Recharge500: String = "9AgIgZaDHD"
    const val Recharge700: String = "Db1ot9Qvjo"
    const val Recharge1000: String = "8xp__sG4wZ"
    const val Recharge1500: String = "8MlpODLATv"
}

object BuildInstance {
    const val STAGING = "staging"
    const val PRODUCTION = "production"
    const val LIVESERVER = "liveserver"
}


object AvenueParams {
    const val COMMAND = "command"
    const val ACCESS_CODE = "access_code"
    const val MERCHANT_ID = "merchant_id"
    const val ORDER_ID = "order_id"
    const val AMOUNT = "amount"
    const val CURRENCY = "currency"
    const val ENC_VAL = "enc_val"
    const val REDIRECT_URL = "redirect_url"
    const val CANCEL_URL = "cancel_url"
    const val RSA_KEY_URL = "rsa_key_url"
}


object ApiEndPoints {
    const val GET_FIRST_RECHARGE = "FlexPlatform/getFirstRechargeDetails"
    const val CHECK_PROFILE_STATUS = "FlexPlatform/checkUserProfileStatus"
    const val SEND_OTP = "FlexPlatform/sendOTP"
    const val VALIDATE_OTP = "FlexPlatform/validateOTP"
    const val USER_PROFILE_DETAILS = "FlexPlatform/userProfileDetails" // to create new user
    const val LOGIN = "FlexPlatform/security/login"
    const val RESET_PASSWORD_SEND_OTP = "FlexPlatform/security/resetpassword/sendOTP"
    const val RESET_PASSWORD_VALIDATE_OTP = "FlexPlatform/security/resetpassword/validateOTP"
    const val HOME_FEATURES_LIST = "FlexPlatform/getFeatureList"
    const val HOROSCOPE = "FlexPlatform/productcatalog/searchByCategory"
    const val SEARCHBYCATEGGORY = "FlexPlatform/productcatalog/searchByCategory"
    const val REDEMPTION_SEARCH = "FlexPlatform/redemptionSearch"
    const val NOTIFICATIONS = "FlexPlatform/getPromoNotification"
    const val PRODUCT_BILLING_DETAILS = "FlexPlatform/productBillingDetails"
    const val ASTROLOGERS_LIST = "FlexPlatform/redemptionSearchWithRating"
    const val WALLET_BALANCE = "FlexPlatform/getMyPointDetails"
    const val OFFER_LIST = "FlexPlatform/getofferlist"
    const val GET_ADDRESS_LIST = "FlexPlatform/getUserAddress"
    const val SAVE_ADDRESS = "FlexPlatform/saveUserAddress"
    const val EDIT_ADDRESS = "FlexPlatform/updateUserAddress"
    const val DELETE_ADDRESS = "FlexPlatform/deleteUserAddress"
    const val PROFILE_PRODUCT_DETAILS = "FlexPlatform/consolidateRedemptionByMode"
    const val REVIEW = "FlexPlatform/getReviewRating"
    const val SUBMIT_REVIEW = "FlexPlatform/addReviewRating"
    const val BUY_PRODUCT = "FlexPlatform/orderWhitegoods"
    const val WALLET_PASSBOOK = "FlexPlatform/getPassbookInfo"
    const val WALLET_TRANSACTIONS = "FlexPlatform/getWalletTransactions"
    const val ASTROLOGER_DETAILS_WITH_RATE = "FlexPlatform/redemptionSearchByName"
    const val PRIVACY_POLICY = "FlexPlatform/fetchPrivacyPolicy"

    const val HOME_STATIC_DATA = "FlexPlatform/getStaticData"

    const val PRODUCT_BILLING = "FlexPlatform/productBilling"

    const val GET_KEY_VALUE = "FlexPlatform/getKeysValue"

    const val SAVE_INTAKE_FORM = "FlexPlatform/userProfileDetails"  // to Save Intake Form

    const val PRE_START_CHAT = "ChatMachine/conversation/preStart"
    const val START_CHAT = "ChatMachine/conversation/start"
    const val SEND_CHAT = "ChatMachine/chat/send"
    const val NEW_CHATS = "ChatMachine/chat/newChat"

    const val END_CHAT = "ChatMachine/conversation/end"
    const val GET_CHAT_HISTORY = "ChatMachine/chat/getChat"

    // API to start Socket Chat
    const val CONVERSATION_START_V2 = "ChatMachine/conversation/v2/start"


    const val GET_BUSY_ASTROLOGERS = "ChatMachine/chat/getBusyPandit"
    const val GET_ASTROLOGERS_STATUS = "FlexPlatform/getProductFlashSale"

    const val GET_LIVE_PANDIT_FOR_USER = "ChatMachine/chat/getLivePanditForUser"
    const val GET_REPORT_STATUS_FOR_USER = "FlexPlatform/searchPendingRememptionByMsisdn"

    const val DOWNLOAD_REPORT = "ChatMachine/file/download/"

    const val REDEEM_POINTS = "FlexPlatform/redeemPoint"  // For Order Report
    const val CALL_REQUEST = "FlexPlatform/pushIvrObd"  // For Call Order

    const val REGISTER_FCM_ID = "GnNotificationManager/v1/user/register"
    const val IN_APP_NOTIFICATION_LIST = "GnNotificationManager/v1/gnNotification/inapp"

    const val SUBSCRIBE_FOR_ONLINE_STATUS = "FlexPlatform/subscribeProductNotification"
    const val SUBSCRIBE_FOR_AVAILABLE_STATUS = "ChatMachine/subscribePanditNotification"

    const val AUTHENTICATE_PROMO_CODE = "FlexPlatform/authenticatePromocode"
    const val AUTHENTICATE_MALL_PROMO_CODE = "FlexPlatform/applyastromallpromo"

    const val PAYMENT_SUMMARY = "FlexPlatform/getpaymentsummary"

    const val FEEDBACK = "FlexPlatform/feedback"

    const val UPLOAD_FILE = "FlexPlatform/uploadFile"

    const val APP_UPDATE = "FlexPlatform/appUpdate"


    //payment
    const val GET_RSA_KEY = "getRSAKey"  //
    const val INITIATE_PAYMENT = "ccavPaymentGateway/initiatePayment"  //
    const val INIT_TRANS = "initTrans"  //
    const val JSON_PAYMENT__URL = "transaction.do"
    const val PARAMETER_SEP = "&"
    const val PARAMETER_EQUALS = "="
    const val TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans"

}


object ApiRequestCodes {
    const val CHECK_PROFILE_STATUS = 1
    const val SEND_OTP = 2
    const val VALIDATE_OTP = 3
    const val ENTER_PASSWORD = 4
    const val LOGIN = 5
    const val RESET_PASSWORD_SEND_OTP = 6
    const val RESET_PASSWORD_VALIDATE_OTP = 7
    const val HOME_DATA = 8
    const val HOROSCOPE = 9
    const val SEARCHBYCATEGGORY = 10
    const val REDEMPTION_SEARCH = 11
    const val NOTIFICATIONS = 12
    const val ASTROLOGERS_LIST = 13
    const val WALLET_BALANCE = 14
    const val GET_ADDRESS_LIST = 15
    const val SAVE_ADDRESS = 16
    const val EDIT_ADDRESS = 17
    const val DELETE_ADDRESS = 18
    const val OUR_STORY = 20
    const val ASTRO_NEWS = 21
    const val PRODUCT_BILLING_DETAILS = 22
    const val PRODUCT_BUY = 23
    const val GET_KEY_VALUE = 23
    const val SAVE_INTAKE_FORM = 24
    const val START_CHAT = 25
    const val GET_CHAT = 26
    const val SEND_CHAT = 27
    const val NEW_CHATS = 28
    const val END_CHAT = 29
    const val GET_BUSY_PANDITS = 30
    const val REDEEM_POINTS = 31
    const val CALL_REQUEST = 32
    const val PASSBOOK = 33
    const val ASTROLOGER_REVIEW = 34
    const val ASTROLOGER_TIME = 35
    const val CHAT_STATUS = 36
    const val ORDER_DETAILS = 37
    const val ALL_ASTROLOGERS_STATUS = 38
    const val SUBMIT_REVIEW = 39
    const val GET_REPORT_STATUS = 40
    const val REGISTER_FCM_ID = 41
    const val SEND_CHAT_REQUEST = 43
    const val SUBSCRIBE_FOR_ONLINE_STATUS = 44
    const val INITIATE_PAYMENT = 45
    const val AUTHENTICATE_VOUCHER = 46
    const val TERMS_POLICY = 47
    const val OFFER_LIST = 48
    const val GET_PROFILE_DATA = 49
    const val UPLOAD_PROFILE_IMAGE = 50
    const val UPDATE_PROFILE_DATA = 51
    const val SUBSCRIBE_FOR_AVAILABLE_STATUS = 52
    const val GET_ASTROLOGER_BUSY_STATUS = 53
    const val SUBMIT_SUPPORT_FORM = 54
    const val RECHARGE = 55

}


object ApiStatusCodes {
    const val SUCCESS = 2000
    const val CHAT_END = 2001
    const val NO_DATA_FOUND = 2002
    const val INVALID_TOKEN = 2009
    const val FAILURE = 5000
}


enum class UserRole(val value: String) {
    ROLE_CLIENT("ROLE_CLIENT")
}


enum class Mode(val value: String) {
    MOBILE("MOBILE"),
    PASSWORD("password")
}

enum class Category(val value: String) {
    HOROSCOPE("Horoscope"),
    HOME("HOME"),
    NEWS("NEWS"),
    MALL("MALL")
}

enum class SubCategory(val value: String) {
    STORY("STORY"),
    VIDEO("VIDEO"),
    NEWS_SUBCATEGORY(""),
    CAT_NAMES("CATNAMES")
}

enum class RedemptionType(val value: String) {
    WHITE("WHITE"),
    REPORT("Report")
}

enum class ProductType(val value: String) {
    WHITE_GOODS("whiteGoods"),
}

enum class KeyDataType(val value: String) {
    TOPIC("topic"),
    LANGUAGE("language")
}


enum class NetworkStatusMessage(val message: String) {
    NO_INTERNET("Please check your internet connection!"),
    SERVER_ERROR("Something went wrong! Please try again."),
    CONNECTION_ERROR("Unable to connect to server. Please try again later.")
}


enum class AstrologersListType(val value: String) {
    CHAT("Chat"),
    CALL("Call"),
    REPORT("Report")
}


enum class OrderHistoryType(val value: String) {
    ALL("All"),
    CHAT("Chat"),
    CALL("Call"),
    REPORT("Report"),
    MALL("Mall")
}


enum class TransactionCategory(val value: String) {
    CHAT("Chat"),
    CALL("Call"),
    REPORT("Report"),
    ADD_MONEY("ADD_MONEY"),
    PROMO_MONEY("PROMO_MONEY"),
    REFUND("Refund")
}


enum class VoucherCategory(val value: String) {
    ADD_MONEY("Add Money"),
    ASTRO_MALL("Astro Mall")
}


enum class ProfileFieldColumn(val value: String) {
    MSISDN("msisdn"),
    USERNAME("username"),
    PASSWORD("password"),
    EMAIL("email_id"),
    USER_CATEGORY("user_category"),
    PROFILE_PIC_URL("profile_pic_url"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),
    GENDER("gender"),
    DOB("dob"),
    BIRTH_PLACE_STORE_NAME("store_name"),
    CITY("city"),
    STATE("state"),
    COUNTRY("country"),
    MARITAL_STATUS("marital_status"),
    OCCUPATION("occupation"),
    COMMENT_DESCRIPTION("description"),
    CONCERN_SHORT_DESCRIPTION("short_description"),
    PROFILE_LANGUAGE("profile_language"),
    PARTNER_NAME_LEVEL1_NAME("level1_name"),
    PARTNER_DETAIL_LEVEL1_ID("level1_id")
}


object ChatApiKeys {
    val appName by lazy { "appName" }
    val from by lazy { "from" }
    val to by lazy { "to" }
    val chatId by lazy { "chatId" }
    val message by lazy { "message" }
}

enum class SunSigns(val value: String) {
    ARIES("aries"),
    TAURUS("taurus"),
    GEMINI("gemini"),
    CANCER("cancer"),
    LEO("leo"),
    VIRGO("virgo"),
    LIBRA("libra"),
    SCORPIO("scorpio"),
    SAGITTARIUS("sagittarius"),
    CAPRICORN("capricorn"),
    AQUARIUS("aquarius"),
    PISCES("pisces")
}


enum class AppUpdateType(val value: String) {
    NO_UPDATE("0"),
    FORCE_UPDATE("1"),
    SOFT_UPDATE("2")
}
