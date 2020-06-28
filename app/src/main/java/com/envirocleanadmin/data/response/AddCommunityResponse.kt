package com.envirocleanadmin.data.response


import com.google.gson.annotations.SerializedName

data class AddCommunityResponse(
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
        @SerializedName("area_comm_id")
        val areaCommId: String? = null,
        @SerializedName("area_name")
        val areaName: String? = null,
        @SerializedName("area_latitude")
        val areaLatitude: String? = null,
        @SerializedName("area_longitude")
        val areaLongitude: String? = null,
        @SerializedName("area_range")
        val areaRange: Int? = null,
        @SerializedName("area_added_by_id")
        val areaAddedById: Int? = null
    )
}