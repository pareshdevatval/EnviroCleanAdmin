package com.envirocleanadmin.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseActivity
import com.envirocleanadmin.base.BaseResponse
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.databinding.ActivityForgotPasswordBinding
import com.envirocleanadmin.di.component.DaggerNetworkLocalComponent
import com.envirocleanadmin.di.component.NetworkLocalComponent
import com.envirocleanadmin.utils.AppConstants
import com.envirocleanadmin.utils.AppUtils
import com.envirocleanadmin.viewmodels.ForgetPasswordModel
import com.envirocleanadmin.utils.ApiParam
import javax.inject.Inject

class ForgetPasswordActivity : BaseActivity<ForgetPasswordModel>() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mViewModel: ForgetPasswordModel


    /*Injecting prefs from DI*/
    @Inject
    lateinit var prefs: Prefs

    /*Injecting apiService*/
    @Inject
    lateinit var apiService: ApiService


    private val email: String by lazy {
        intent.getStringExtra(AppConstants.EMAIL)
    }

    companion object {
        fun newInstance(context: Context, email: String): Intent {
            val intent = Intent(context, ForgetPasswordActivity::class.java)
            intent.putExtra(AppConstants.EMAIL, email)
            return intent
        }
    }

    override fun getViewModel(): ForgetPasswordModel {
        mViewModel = ViewModelProvider(this).get(ForgetPasswordModel::class.java)
        return mViewModel

    }

    override fun internetErrorRetryClicked() {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {

        val requestsComponent: NetworkLocalComponent = DaggerNetworkLocalComponent
            .builder()
            .networkComponent(getNetworkComponent())
            .localDataComponent(getLocalDataComponent())
            .build()
        requestsComponent.injectForgetPasswordActivity(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        init()
    }

    fun init() {
        binding.forgotPassword = this
        mViewModel.setInjectable(apiService, prefs)
        mViewModel.getForgetPasswordResponse().observe(this, forgetPasswordResponseObserver)
        binding.edtEmailPhone.setText(email)
        setToolBar("", R.color.transprent)

    }

    fun onSendClick() {
        if (binding.edtEmailPhone.text.toString() == "") {
            AppUtils.showSnackBar(binding.btnSend, "Please enter email")
        } else if (!isEmailValid(binding.edtEmailPhone.text.toString())) {
            AppUtils.showSnackBar(binding.btnSend, "Please enter valid email")
        } else {
            val requestParams = HashMap<String, String>()
            requestParams[ApiParam.A_EMAIL] = binding.edtEmailPhone.text.toString()
            requestParams[ApiParam.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_ANDROID
            mViewModel.callForgetPasswordApi(requestParams)
        }
    }

    private fun setToolBar(title: String, bgColor: Int) {
        // setting toolbar title
        setToolbarTitle(title)
        // toolbar color

        setToolbarColor(bgColor)

        // toolbar left icon and its click listener
        setToolbarLeftIcon(R.drawable.ic_back_black, object : ToolbarLeftImageClickListener {
            override fun onLeftImageClicked() {
                onBackPressed()
            }
        })

    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private val forgetPasswordResponseObserver = Observer<BaseResponse> { resoponse ->
        if (resoponse.status) {
            AppUtils.showSnackBar(binding.btnSend, resoponse.message)
        } else {
            AppUtils.showSnackBar(binding.btnSend, resoponse.message)
        }
    }

}
