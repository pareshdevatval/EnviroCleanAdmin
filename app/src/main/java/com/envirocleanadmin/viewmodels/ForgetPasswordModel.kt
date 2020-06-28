package com.envirocleanadmin.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.envirocleanadmin.EnviroCleanAdminApp
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseResponse
import com.envirocleanadmin.base.BaseViewModel
import com.envirocleanadmin.utils.AppUtils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

class ForgetPasswordModel(application: Application) : BaseViewModel(application) {

    /*RxJava Subscription object for api calling*/
    private var subscription: Disposable? = null

    private val forgetPassResponse: MutableLiveData<BaseResponse> by lazy {
        MutableLiveData<BaseResponse>()
    }

    fun getForgetPasswordResponse(): LiveData<BaseResponse> {
        return forgetPassResponse
    }


    fun callForgetPasswordApi(params: HashMap<String, String>) {
        if (AppUtils.hasInternet(getApplication())) {
            Log.e("----->",""+params)
            subscription = apiServiceObj
                .apiForgotPassword(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onApiStart() }
                .doOnTerminate { onApiFinish() }
                .subscribe(this::handleResponse, this::handleError)
        } else {
            onInternetError()
        }
    }

    private fun handleResponse(response: BaseResponse) {
        forgetPassResponse.value = response
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