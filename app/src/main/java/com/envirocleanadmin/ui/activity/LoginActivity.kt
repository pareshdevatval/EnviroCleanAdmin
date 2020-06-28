package com.envirocleanadmin.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseActivity
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.response.LoginResponse
import com.envirocleanadmin.databinding.ActivityLoginBinding
import com.envirocleanadmin.di.component.DaggerNetworkLocalComponent
import com.envirocleanadmin.di.component.NetworkLocalComponent
import com.envirocleanadmin.utils.AppUtils
import com.envirocleanadmin.utils.validator.ValidationErrorModel
import com.envirocleanadmin.viewmodels.LoginViewModel
import javax.inject.Inject

/**
 * [onBackPressed]
 * */
class LoginActivity : BaseActivity<LoginViewModel>(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mViewModel: LoginViewModel


    private var isShowPassword = false
    /*Injecting prefs from DI*/
    @Inject
    lateinit var prefs: Prefs

    /*Injecting apiService*/
    @Inject
    lateinit var apiService: ApiService


    var from: String = ""

    companion object {
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val requestsComponent: NetworkLocalComponent = DaggerNetworkLocalComponent
            .builder()
            .networkComponent(getNetworkComponent())
            .localDataComponent(getLocalDataComponent())
            .build()
        requestsComponent.injectLoginActivity(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginActivity = this

        init()
    }

    private fun init() {
        mViewModel.setInjectable(apiService, prefs)
        setHideShowPassword()
        mViewModel.getLoginResponse().observe(this, loginResponseObserver)
        mViewModel.getValidationError().observe(this, validationObserver)

    }

    private val loginResponseObserver = Observer<LoginResponse> {
        it?.let {
            it.status?.let { status ->
                it
                if (status) {
                    prefs.userDataModel = it
                    prefs.isLoggedIn = true
                    startActivity(CommunityActivity.newInstance(this))
                    AppUtils.startFromRightToLeft(this)
                } else {
                    it.message?.let { message ->
                        it
                        AppUtils.showSnackBar(binding.btnSignIn, message)
                    }
                }
            }

        }

    }
    private val validationObserver = Observer<ValidationErrorModel> {
        AppUtils.showSnackBar(binding.btnSignIn, getString(it.msg))
    }

    fun onSignInClick() {
        mViewModel.checkValidation(
            binding.edtEmailPhone.text.toString(),
            binding.edtPassword.text.toString()
        )
    }

    fun onForgotPasswordClick() {
        val p1 = androidx.core.util.Pair(binding.edtEmailPhone as View, "text")
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1)
        startActivity(
            ForgetPasswordActivity.newInstance(
                this,
                binding.edtEmailPhone.text.toString()
            ), options.toBundle()
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setHideShowPassword() {
        binding.edtPassword.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= binding.edtPassword.right - binding.edtPassword.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        if (isShowPassword) {
                            binding.edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.ic_password),
                                null,
                                getDrawable(R.drawable.ic_show_password),
                                null
                            )
                            isShowPassword = false
                            binding.edtPassword.clearFocus()
                            binding.edtPassword.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
                            binding.edtPassword.setTextAppearance(
                                this@LoginActivity,
                                R.style.editTextStyle
                            )
                        } else {
                            binding.edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.ic_password),
                                null,
                                getDrawable(R.drawable.ic_password_hide),
                                null
                            )
                            isShowPassword = true
                            binding.edtPassword.clearFocus()
                            binding.edtPassword.inputType =
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.edtPassword.setSelection(binding.edtPassword.text!!.length)
                            binding.edtPassword.setTextAppearance(
                                this@LoginActivity,
                                R.style.editTextStyle
                            )

                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun getViewModel(): LoginViewModel {
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return mViewModel
    }

    override fun internetErrorRetryClicked() {
    }

    override fun onClick(v: View?) {
    }

}
