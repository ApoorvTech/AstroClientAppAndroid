package com.myastrotell.pojo.response


data class TermsAndPolicyResponse(
    var appTitleMaster: List<AppTitleMaster>?,
    var privacyPolicyData: List<PrivacyPolicyData>?
)

data class AppTitleMaster(
    var clientId: String?,
    var id: Int?,
    var language: String?,
    var titleImageUrl: String?,
    var titleKey: String?,
    var titleMessage: String?,
    var titleType: String?,
    var updateTimestamp: String?
)

data class PrivacyPolicyData(
    var campaignId: Int?,
    var clientId: String?,
    var language: String?,
    var policyDescription: String?,
    var policyId: Int?,
    var policyTitle: String?,
    var updateTimestamp: String?
)

