package com.envirocleanadmin.data.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("result")
    val result: Result? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: Boolean? = null,
    @SerializedName("code")
    val code: Int? = null
) {
    data class Result(
        @SerializedName("a_id")
        val aId: Int? = null,
        @SerializedName("a_first_name")
        val aFirstName: String? = null,
        @SerializedName("a_last_name")
        val aLastName: String? = null,
        @SerializedName("a_email")
        val aEmail: String? = null,
        @SerializedName("a_mobile_number")
        val aMobileNumber: Any? = null,
        @SerializedName("a_mailing_address")
        val aMailingAddress: Any? = null,
        @SerializedName("a_type")
        val aType: Int? = null,
        @SerializedName("a_image")
        val aImage: String? = null,
        @SerializedName("a_last_login")
        val aLastLogin: String? = null,
        @SerializedName("a_status")
        val aStatus: Int? = null,
        @SerializedName("a_created_at")
        val aCreatedAt: String? = null,
        @SerializedName("a_updated_at")
        val aUpdatedAt: String? = null,
        @SerializedName("a_deleted_at")
        val aDeletedAt: String? = null,
        @SerializedName("token")
        val token: String? = null
    )
}