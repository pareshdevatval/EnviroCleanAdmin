package com.envirocleanadmin.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.envirocleanadmin.EnviroCleanAdminApp
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseViewModel
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.data.response.AddCommunityResponse
import com.envirocleanadmin.data.response.CommunityAreaListResponse
import com.envirocleanadmin.utils.ApiParam
import com.envirocleanadmin.utils.AppConstants
import com.envirocleanadmin.utils.AppUtils
import com.google.android.gms.maps.model.LatLng

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

/**
 * Created by imobdev on 21/2/20
 */
class CommunityDetailsViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var appDatabase: AppDatabase

    fun setAppDatabase(appDatabase: AppDatabase) {
        this.appDatabase = appDatabase
    }

    /*RxJava Subscription object for api calling*/
    private var subscription: Disposable? = null

    private val comunityAreaListResponse: MutableLiveData<CommunityAreaListResponse> by lazy {
        MutableLiveData<CommunityAreaListResponse>()
    }

    fun getCommunityAreaListResponse(): LiveData<CommunityAreaListResponse> {
        return comunityAreaListResponse
    }

    private val addAreaResponse: MutableLiveData<AddCommunityResponse> by lazy {
        MutableLiveData<AddCommunityResponse>()
    }

    fun getAddCommunityAreaResponse(): LiveData<AddCommunityResponse> {
        return addAreaResponse
    }

    fun callListOfCommApi(commId: String) {

        if (AppUtils.hasInternet(getApplication())) {
            val params: HashMap<String, Any> = HashMap()
            params[ApiParam.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_ANDROID
            params[ApiParam.COMM_ID] = commId
            subscription = apiServiceObj
                .apiCommunityArea(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onApiStart() }
                .doOnTerminate { onApiFinish() }
                .subscribe(this::handleResponse, this::handleError)

        } else {
            onInternetError()
        }
    }

    fun callAddCommApi(
        commId: String,
        latLong: LatLng,
        km: Int,
        areaName: String
    ) {

        if (AppUtils.hasInternet(getApplication())) {
            val params: HashMap<String, Any> = HashMap()
            params[ApiParam.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_ANDROID
            params[ApiParam.CENTER_LAT] = latLong.latitude
            params[ApiParam.CENTER_LONG] = latLong.longitude
            params[ApiParam.AREA_NAME] = areaName
            params[ApiParam.RADIUS] = km
            params[ApiParam.ID] = commId
            subscription = apiServiceObj
                .apiAddCommunityArea(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onApiStart() }
                .doOnTerminate { onApiFinish() }
                .subscribe(this::handleAddCommAreaResponse, this::handleError)

        } else {
            onInternetError()
        }
    }

    fun callEditCommApi(
        result: CommunityAreaListResponse.Result,
        areaName: String,
        km: Int,
        commId: String
    ) {

        if (AppUtils.hasInternet(getApplication())) {
            val params: HashMap<String, Any> = HashMap()
            params[ApiParam.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_ANDROID
            params[ApiParam.CENTER_LAT] = result.areaLatitude!!.toDouble()
            params[ApiParam.CENTER_LONG] = result.areaLongitude!!.toDouble()
            params[ApiParam.AREA_NAME] = areaName
            params[ApiParam.RADIUS] = km
            params[ApiParam.ID] = commId
            params[ApiParam.AREA_ID] = result.areaId!!
            subscription = apiServiceObj
                .apiEditCommunityArea(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onApiStart() }
                .doOnTerminate { onApiFinish() }
                .subscribe(this::handleAddCommAreaResponse, this::handleError)

        } else {
            onInternetError()
        }
    }

    private fun handleResponse(response: CommunityAreaListResponse) {
        comunityAreaListResponse.value = response
    }

    private fun handleAddCommAreaResponse(response: AddCommunityResponse) {
        apiErrorMessage.value = response.message
        addAreaResponse.value = response
    }

    private fun handleError(error: Throwable) {
        onApiFinish()
        if (error is SocketTimeoutException) {
            AppUtils.showToast(
                getApplication(),
                getApplication<EnviroCleanAdminApp>().getString(R.string.connection_timed_out)
            )
        }
        //apiErrorMessage.value = error.localizedMessage
    }


    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}