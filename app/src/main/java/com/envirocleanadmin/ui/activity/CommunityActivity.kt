package com.envirocleanadmin.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.envirocleanadmin.R
import com.envirocleanadmin.adapter.CommunityAdapter
import com.envirocleanadmin.base.BaseActivity
import com.envirocleanadmin.base.BaseBindingAdapter
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.response.ListOfCommunityResponse
import com.envirocleanadmin.databinding.ActivityCommunityBinding
import com.envirocleanadmin.di.component.DaggerNetworkLocalComponent
import com.envirocleanadmin.di.component.NetworkLocalComponent
import com.envirocleanadmin.utils.AppUtils
import com.envirocleanadmin.utils.RecycleViewCustom
import com.envirocleanadmin.viewmodels.CommunityViewModel
import javax.inject.Inject


class CommunityActivity : BaseActivity<CommunityViewModel>(), View.OnClickListener,
    RecycleViewCustom.onLoadMore, RecycleViewCustom.onSwipeToRefresh,
    BaseBindingAdapter.ItemClickListener<ListOfCommunityResponse.Result?> {


    private lateinit var binding: ActivityCommunityBinding
    private lateinit var mViewModel: CommunityViewModel

    lateinit var listOfCommunityResponse: ListOfCommunityResponse

    /*Injecting prefs from DI*/
    @Inject
    lateinit var prefs: Prefs

    /*Injecting apiService*/
    @Inject
    lateinit var apiService: ApiService
    var CURRENT_PAGE: Int = 1

    var from: String = ""

    companion object {
        val REQUEST_CODE = 105
        var IS_REFRESH = false
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, CommunityActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val requestsComponent: NetworkLocalComponent = DaggerNetworkLocalComponent
            .builder()
            .networkComponent(getNetworkComponent())
            .localDataComponent(getLocalDataComponent())
            .build()
        requestsComponent.injectCommunityActivity(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community)

        init()
    }

    private fun init() {
        mViewModel.setInjectable(apiService, prefs)
        setToolBar(getString(R.string.lbl_community), R.color.pantone_072)
        mViewModel.getListOfCommunityResponse().observe(this, commListResponseObserver)
        val adapter = CommunityAdapter()
        adapter.filterable = true
        adapter.itemClickListener = this
        binding.rvView.setAdepter(adapter)
        binding.rvView.onLoadMoreItemClick = this
        binding.rvView.swipeToRefreshItemClick = this
        apiCall()
    }

    private fun setToolBar(title: String, bgColor: Int) {
        // setting toolbar title
        setToolbarTitle(title)
        // toolbar color

        setToolbarColor(bgColor)

        // toolbar left icon and its click listener
        setToolbarLeftIcon(R.drawable.ic_logout_white, object : ToolbarLeftImageClickListener {
            override fun onLeftImageClicked() {
                onBackPressed()
            }
        })
        setToolbarRightIcon(
            R.drawable.ic_search,
            object : ToolbarRightImageClickListener {
                override fun onRightImageClicked() {
                    showSearchBar(R.color.pantone_072)
                    setSearch()
                }
            })

    }

    private fun setSearch() {
        val editText = getEditTextView()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (binding.rvView.rvItems.adapter as CommunityAdapter).filter(
                    s.toString().toLowerCase()
                )
            }
        })



        editText?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        hideSearchBar()
                    }
                }
                return false
            }
        })

    }

    private fun apiCall() {
        mViewModel.callListOfCommApi(1, true)
    }

    private val commListResponseObserver = Observer<ListOfCommunityResponse> {
        it.status?.let { status ->
            if (it.status) {
                it.result?.let { result ->
                    if (binding.rvView.CURENT_PAGE == 1) {
                        listOfCommunityResponse = it
                        binding.rvView.setTotalPages(it.pagination!!.lastPage!!)

                        (binding.rvView.rvItems.adapter as CommunityAdapter).setItem(it.result)
                        (binding.rvView.rvItems.adapter as CommunityAdapter).notifyDataSetChanged()
                    } else {
                        (binding.rvView.rvItems.adapter as CommunityAdapter).removeFooterProgressItem(
                            binding.rvView
                        )
                        (binding.rvView.rvItems.adapter as CommunityAdapter).addItems(it.result)
                        (binding.rvView.rvItems.adapter as CommunityAdapter).notifyDataSetChanged()

                    }
                }

            }
        }
    }

    override fun onSwipeToRefresh() {
        apiCall()
    }

    override fun onLoadMore() {
        mViewModel.callListOfCommApi(binding.rvView.CURENT_PAGE, false)
    }

    override fun onItemClick(view: View, data: ListOfCommunityResponse.Result?, position: Int) {
        data?.let {
            startActivityForResult(
                CommunityDetailsActivity.newInstance(
                    this,
                    data,
                    listOfCommunityResponse
                ), REQUEST_CODE
            )
            AppUtils.startFromRightToLeft(this)

        }
    }

    override fun getViewModel(): CommunityViewModel {
        mViewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        return mViewModel
    }

    override fun internetErrorRetryClicked() {
    }

    override fun onClick(v: View?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getBooleanExtra("result", false)
                if (IS_REFRESH) {

                    apiCall()
                }
            }
        }
    }
}
