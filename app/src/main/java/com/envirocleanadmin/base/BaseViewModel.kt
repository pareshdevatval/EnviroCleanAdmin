package com.envirocleanadmin.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.envirocleanadmin.R
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs


/* A Base class for all ViewModels classes */
/**
 * Created by Keshu Odedara on 06-05-2020.
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val apiErrorMessage: MutableLiveData<String> = MutableLiveData()
    val loadingVisibility: MutableLiveData<Boolean> = MutableLiveData()

    protected lateinit var apiServiceObj: ApiService
    protected lateinit var prefsObj: Prefs

    fun onApiStart() {
        loadingVisibility.value = true
        errorMessage.value = null
    }

    fun onInternetError() {
        errorMessage.value = R.string.msg_no_internet
    }

    fun onApiFinish() {
        loadingVisibility.value = false
    }

    fun onApiFinishPost() {
        loadingVisibility.postValue(false)
    }

    fun setInjectable(apiService: ApiService, prefs: Prefs) {
        this.apiServiceObj = apiService
        this.prefsObj = prefs
    }

    fun injectApiService(apiService: ApiService) {
        this.apiServiceObj = apiService
    }

    fun injectPrefs(prefs: Prefs) {
        this.prefsObj = prefs
    }
}