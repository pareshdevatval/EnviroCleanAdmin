package com.envirocleanadmin.base

import com.google.gson.annotations.SerializedName


/* A Base class for Base API Response */
open class BaseResponse {

    @SerializedName("status")
    var status: Boolean = false
    @SerializedName("message")
    var message: String = ""
    @SerializedName("code")
    var code: Int? = null
    var totalCount: Int = 0
}