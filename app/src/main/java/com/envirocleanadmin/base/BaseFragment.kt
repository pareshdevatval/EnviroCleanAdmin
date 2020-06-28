package com.envirocleanadmin.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.envirocleanadmin.EnviroCleanAdminApp
import com.envirocleanadmin.R
import com.google.android.material.snackbar.Snackbar

import com.envirocleanadmin.utils.AppUtils

/* A Base class for all fragments */
/**
 * Created by Keshu Odedara on 06-05-2020.
 */
abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    private lateinit var mViewModel: T
    private var progressDialog: Dialog? = null
    private var errorSnackbar: Snackbar? = null
    private val errorClickListener = View.OnClickListener { internetErrorRetryClicked() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getViewModel()

        mViewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (errorMessage != null) showError(errorMessage) else hideError()
        })

        mViewModel.loadingVisibility.observe(this, Observer { loadingVisibility ->
            loadingVisibility?.let { if (loadingVisibility) showProgress() else hideProgress() }
        })

        mViewModel.apiErrorMessage.observe(this, Observer { apiError ->
            apiError?.let { AppUtils.showSnackBar(this.view!!, it) }
        })

    }

    private fun showError(@StringRes errorMessage: Int) {
        errorSnackbar = Snackbar.make(this.view!!, errorMessage, Snackbar.LENGTH_LONG)
        //errorSnackbar?.setAction(R.string.action_retry, errorClickListener)
        errorSnackbar?.show()
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): T

    abstract fun internetErrorRetryClicked()

    /* [START] show progress bar*/
    @SuppressLint("InflateParams")
    fun showProgress(showText: Boolean = false) {
        /*if (progressDialog == null) {
            progressDialog = Dialog(context!!)
            // dialog without title
            progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        // inflating and seeting view of custom dialog
        val view = LayoutInflater.from(context).inflate(R.layout.app_loading_dialog, null, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)

        // applying rotate animation
        ObjectAnimator.ofFloat(imageView, View.ROTATION, 360f, 0f).apply {
            repeatCount = ObjectAnimator.INFINITE
            duration = 1500
            interpolator = LinearInterpolator()
            start()
        }
        progressDialog?.setContentView(view)

        // setting background of dialog as transparent
        val window = progressDialog?.window
        window?.setBackgroundDrawable(
            ContextCompat.getDrawable(context!!, android.R.color
                .transparent))

        // preventing outside touch and setting cancelable false
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()*/
        activity?.let {
            (it as BaseActivity<*>).showProgress()
        }
    }


    /* [END] show progress bar*/

    fun hideProgress() {
        //progressDialog?.dismiss()
        activity?.let {
            (it as BaseActivity<*>).hideProgress()
        }
    }



    /* [START] Check if an active internet connection is present or not*/
    /* return boolean value true or false */
    fun isInternetAvailable(): Boolean {
        // getting Connectivity service as ConnectivityManager
        return AppUtils.hasInternet(context!!)
    }

    private fun getApplication() = (activity?.applicationContext as EnviroCleanAdminApp)

    fun getAppComponent() = getApplication().getAppComponent()

    fun getLocalDataComponent() = getApplication().getLocalDataComponent()

    fun getNetworkComponent() = getApplication().getNetworkComponent()

    fun hideToolbar() {
        activity?.let {
            (it as BaseActivity<*>).hideToolbar()
        }
    }

    // toolabr title
    fun setToolbarTitle(title: Int) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarTitle(title)
        }
    }

    // toolabr title
    fun setToolbarTitle(title: String) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarTitle(title)
        }
    }

    // set icon of toolbar left icon
    fun setToolbarLeftIcon(resourceId: Int, toolbarLeftClickListener: BaseActivity.ToolbarLeftImageClickListener) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarLeftIcon(resourceId, toolbarLeftClickListener)
        }
    }

    // set toolbar right icon and implement its click
    fun setToolbarRightIcon(resourceId: Int, toolbarRightClickListener: BaseActivity.ToolbarRightImageClickListener) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarRightIcon(resourceId, toolbarRightClickListener)
        }
    }

    fun setToolbarRight1Icon(resourceId: Int, toolbarRight1ClickListener: BaseActivity.ToolbarRight1ImageClickListener) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarRight1Icon(resourceId, toolbarRight1ClickListener)
        }
    }

    // hide toolbar right icon when not needed
    fun hideToolbarRightIcon() {
        activity?.let {
            (it as BaseActivity<*>).hideToolbarRightIcon()
        }
    }

    fun showToolbarRightIcon() {
        activity?.let {
            (it as BaseActivity<*>).showToolbarRightIcon()
        }
    }

    // hide toolbar left icon when not needed
    fun hideToolbarLeftIcon() {
        activity?.let {
            (it as BaseActivity<*>).hideToolbarLeftIcon()
        }
    }

    fun setToolbarColor(color: Int) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarColor(color)
        }
    }


    fun getEditTextView(): EditText? {
        if(activity != null) {
            return (activity as BaseActivity<*>).getEditTextView()
        } else {
            return null
        }
    }

    fun showCartView() {
        if(activity != null) {
            (activity as BaseActivity<*>).showCartView()
        }
    }
}