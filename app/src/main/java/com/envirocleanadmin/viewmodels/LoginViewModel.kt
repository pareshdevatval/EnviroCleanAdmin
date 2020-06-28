package com.envirocleanadmin.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.envirocleanadmin.EnviroCleanAdminApp
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseViewModel
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.data.response.LoginResponse
import com.envirocleanadmin.utils.AppConstants
import com.envirocleanadmin.utils.AppUtils
import com.envirocleanadmin.utils.validator.ValidationErrorModel
import com.envirocleanadmin.utils.validator.Validator
import com.envirocleanadmin.utils.ApiParam

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

/**
 * Created by imobdev on 21/2/20
 */
class LoginViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var appDatabase: AppDatabase

    fun setAppDatabase(appDatabase: AppDatabase) {
        this.appDatabase = appDatabase
    }

    /*RxJava Subscription object for api calling*/
    private var subscription: Disposable? = null

    private val loginResponse: MutableLiveData<LoginResponse> by lazy {
        MutableLiveData<LoginResponse>()
    }

    fun getLoginResponse(): LiveData<LoginResponse> {
        return loginResponse
    }
    private val validationError: MutableLiveData<ValidationErrorModel> by lazy {
        MutableLiveData<ValidationErrorModel>()
    }


    fun getValidationError(): LiveData<ValidationErrorModel> {
        return validationError
    }

    fun checkValidation(mEmail:String,password:String){

        Validator.validateEmail(mEmail)?.let {
            validationError.value=it
            return
        }
        Validator.validatePassword(password)?.let {
            validationError.value=it
            return
        }
        val params :HashMap<String,String> = HashMap()
        params[ApiParam.A_USERNAME]=mEmail
        params[ApiParam.A_PASSWORD]=password
        params[ApiParam.DEVICE_TOKEN]=""
        params[ApiParam.DEVICE_TYPE]=AppConstants.DEVICE_TYPE_ANDROID
        callLoginApi(params)
    }



    fun callLoginApi(params: HashMap<String, String>) {
        if (AppUtils.hasInternet(getApplication())) {
            subscription = apiServiceObj
                .apiLogin(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { onApiStart() }
                .doOnTerminate { onApiFinish() }
                .subscribe(this::handleResponse, this::handleError)

        } else {
            onInternetError()
        }
    }

    private fun handleResponse(response: LoginResponse) {
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