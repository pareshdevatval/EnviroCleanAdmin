package com.envirocleanadmin.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.envirocleanadmin.EnviroCleanAdminApp
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseViewModel
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.data.response.ListOfCommunityResponse
import com.envirocleanadmin.utils.ApiParam
import com.envirocleanadmin.utils.AppConstants
import com.envirocleanadmin.utils.AppUtils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

/**
 * Created by imobdev on 21/2/20
 */
class CommunityViewModel(application: Application) : BaseViewModel(application) {

    /*RxJava Subscription object for api calling*/
    private var subscription: Disposable? = null

    private val loginResponse: MutableLiveData<ListOfCommunityResponse> by lazy {
        MutableLiveData<ListOfCommunityResponse>()
    }

    fun getListOfCommunityResponse(): LiveData<ListOfCommunityResponse> {
        return loginResponse
    }

    fun callListOfCommApi(pages: Int=1,showProgress: Boolean = true) {
        if (AppUtils.hasInternet(getApplication())) {
            val params:HashMap<String,Any> = HashMap()
            params[ApiParam.DEVICE_TYPE]= AppConstants.DEVICE_TYPE_ANDROID
            params[ApiParam.LIMIT]= AppConstants.LIMIT
            params[ApiParam.PAGE]= pages
            subscription = apiServiceObj
                .apiCommunityList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { if (showProgress) onApiStart() }
                .doOnTerminate { if (showProgress) onApiFinish() }
                .subscribe(this::handleResponse, this::handleError)

        } else {
            onInternetError()
        }
    }

    private fun handleResponse(response: ListOfCommunityResponse) {
        loginResponse.value = response
    }

    private fun handleError(error: Throwable) {
        onApiFinish()
        if(error is SocketTimeoutException) {
            AppUtils.showToast(getApplication(), getApplication<EnviroCleanAdminApp>().getString(R.string.connection_timed_out))
        }
        //apiErrorMessage.value = error.localizedMessage
    }



    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}