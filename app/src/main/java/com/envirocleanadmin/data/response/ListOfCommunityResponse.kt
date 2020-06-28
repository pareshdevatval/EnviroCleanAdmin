package com.envirocleanadmin.data.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize

data class ListOfCommunityResponse(
    @SerializedName("pagination")
    val pagination: Pagination? = null,
    @SerializedName("result")
    val result: ArrayList<Result?>,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: Boolean? = null
) : Parcelable {
    @Parcelize

    data class Pagination(
        @SerializedName("total")
        val total: Int? = null,
        @SerializedName("lastPage")
        val lastPage: Int? = null,
        @SerializedName("perPage")
        val perPage: String? = null,
        @SerializedName("currentPage")
        val currentPage: Int? = null
    ) : Parcelable

    @Parcelize

    data class Result(
        @SerializedName("comm_id")
        val commId: Int? = null,
        @SerializedName("comm_name")
        val commName: String? = null,
        @SerializedName("comm_latitude")
        val commLatitude: String? = null,
        @SerializedName("comm_longitude")
        val commLongitude: String? = null,
        @SerializedName("comm_range")
        val commRange: String? = null,
        @SerializedName("region_name")
        val regionName: String? = null,
        @SerializedName("area_count")
        val areaCount: Int? = null
    ) : Parcelable
}