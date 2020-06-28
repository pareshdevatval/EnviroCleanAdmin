package com.envirocleanadmin.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appolica.interactiveinfowindow.InfoWindow
import com.appolica.interactiveinfowindow.InfoWindow.MarkerSpecification
import com.appolica.interactiveinfowindow.InfoWindowManager
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment
import com.envirocleanadmin.R
import com.envirocleanadmin.base.BaseActivity
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.response.AddCommunityResponse
import com.envirocleanadmin.data.response.CommunityAreaListResponse
import com.envirocleanadmin.data.response.ListOfCommunityResponse
import com.envirocleanadmin.databinding.ActivityCommunityDetailsBinding
import com.envirocleanadmin.di.component.DaggerNetworkLocalComponent
import com.envirocleanadmin.di.component.NetworkLocalComponent
import com.envirocleanadmin.geofancing.Reminder
import com.envirocleanadmin.utils.AppConstants
import com.envirocleanadmin.utils.AppUtils
import com.envirocleanadmin.utils.MyLocationProvider
import com.envirocleanadmin.utils.custommarker.MarkerInfoWindowFragment
import com.envirocleanadmin.viewmodels.CommunityDetailsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class CommunityDetailsActivity : BaseActivity<CommunityDetailsViewModel>(), View.OnClickListener,
    OnMapReadyCallback, MyLocationProvider.MyLocationListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener, InfoWindowManager.WindowShowListener,
    MarkerInfoWindowFragment.MarkerEditAreaClickListener {


    private lateinit var binding: ActivityCommunityDetailsBinding
    private lateinit var mViewModel: CommunityDetailsViewModel

    private lateinit var mMap: GoogleMap

    /*Injecting prefs from DI*/
    @Inject
    lateinit var prefs: Prefs

    /*Injecting apiService*/
    @Inject
    lateinit var apiService: ApiService

    var isRefresh = false
    private val communityName: String by lazy {
        intent.getParcelableExtra<ListOfCommunityResponse.Result>(AppConstants.COMMUNITY_DATA)!!.commName!!
    }

    var locationProvider: MyLocationProvider? = null

    lateinit var listOfCommunityResponse: ListOfCommunityResponse
    private lateinit var areaList: ArrayList<CommunityAreaListResponse.Result?>
    lateinit var communityId: String
    var clickMap = false
    lateinit var marker: Marker
    lateinit var mLatLng: LatLng
    lateinit var communityData: ListOfCommunityResponse.Result
    private var infoWindowManager: InfoWindowManager? = null
    private lateinit var infoWindowList: ArrayList<InfoWindow>

    companion object {
        fun newInstance(
            context: Context,
            community: ListOfCommunityResponse.Result,
            listOfCommunityResponse: ListOfCommunityResponse
        ): Intent {
            val intent = Intent(context, CommunityDetailsActivity::class.java)
            intent.putExtra(AppConstants.COMMUNITY_DATA, community)
            intent.putExtra(AppConstants.COMMUNITY_LIST, listOfCommunityResponse)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        infoWindowList = ArrayList<InfoWindow>()
        val requestsComponent: NetworkLocalComponent = DaggerNetworkLocalComponent
            .builder()
            .networkComponent(getNetworkComponent())
            .localDataComponent(getLocalDataComponent())
            .build()
        requestsComponent.injectCommunityDetailsActivity(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_details)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as MapInfoWindowFragment

        infoWindowManager = mapFragment.infoWindowManager()
        infoWindowManager!!.setHideOnFling(true)

        mapFragment.getMapAsync(this)
        init()
    }

    private fun init() {
        clearGeoFanchingData()
        getLocation()
        mViewModel.setInjectable(apiService, prefs)
        listOfCommunityResponse = intent.getParcelableExtra(AppConstants.COMMUNITY_LIST)!!
        communityData = intent.getParcelableExtra(AppConstants.COMMUNITY_DATA)!!
        communityId = communityData.commId.toString()
        setToolBar(communityName, R.color.pantone_072)
        mViewModel.getCommunityAreaListResponse().observe(this, commAreaListResponseObserver)
        mViewModel.getAddCommunityAreaResponse().observe(this, addCommAreaResponseObserver)
        mViewModel.callListOfCommApi(communityId)
        initSpinnerViolationReason()

    }

    private fun clearGeoFanchingData() {
        for (reminder in getRepository().getAll()) {
            removeReminder(reminder)
        }
    }

    private fun getLocation() {
        locationProvider = MyLocationProvider(this, this)
        locationProvider?.init()
    }

    private fun setToolBar(title: String, bgColor: Int) {
        // setting toolbar title
        setToolbarSpinner(title)
        // toolbar color

        setToolbarColor(bgColor)

        // toolbar left icon and its click listener
        setToolbarLeftIcon(R.drawable.ic_back_white, object : ToolbarLeftImageClickListener {
            override fun onLeftImageClicked() {
                onBackPressed()
            }
        })
        setToolbarRightIcon(
            R.drawable.ic_plus_white,
            object : ToolbarRightImageClickListener {
                override fun onRightImageClicked() {
                    showAreaAddDialog(null)
                }
            })

    }

    var coutAddDataGeoFanching = 0

    private val commAreaListResponseObserver = Observer<CommunityAreaListResponse> {
        areaList = it.result
        if (areaList.size > 0) {
            for (i in 0 until it.result.size) {
                if (i == 0) {
                    val reminder = Reminder(latLng = null, radius = null, message = null)
                    coutAddDataGeoFanching += 1
                    reminder.latLng = LatLng(
                        it.result[i]!!.areaLatitude!!.toDouble(),
                        it.result[i]!!.areaLongitude!!.toDouble()
                    )
                    reminder.message = it.result[i]!!.areaName
                    reminder.radius = it.result[i]!!.areaRange!!.toDouble() * 1000
                    val latLong = LatLng(
                        it.result[i]!!.areaLatitude!!.toDouble(),
                        it.result[i]!!.areaLongitude!!.toDouble()
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 18.5f));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18.5f), 2000, null);
                    //addReminder(reminder, it.result)
                }
                addMarkerOnMap(it.result[i]!!)
            }
        } else {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        communityData.commLatitude!!.toDouble(),
                        communityData.commLongitude!!.toDouble()
                    ), 18.5f
                )
            )
        }
    }

    private fun addMarkerOnMap(result: CommunityAreaListResponse.Result) {

        val latLng = LatLng(
            result.areaLatitude!!.toDouble(),
            result.areaLongitude!!.toDouble()
        )

        val markerOptions = MarkerOptions().position(latLng)

        markerOptions.icon(
            AppUtils.vectorToBitmap(
                resources,
                R.drawable.ic_pin
            )
        )

        drawCircleOnMap(
            latLng,
            result.areaRange!!.toDouble(),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        val offsetX = resources.getDimension(R.dimen._5sdp).toInt()
        val offsetY = resources.getDimension(R.dimen._39sdp).toInt()

        val markerSpec = MarkerSpecification(offsetX, offsetY)

        val marker = mMap.addMarker(markerOptions)
        marker.tag = result
        marker.zIndex = infoWindowList.size.toFloat()

        val markerInfoWindowFragment = MarkerInfoWindowFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstants.COMMUNITY_DATA, result)
        markerInfoWindowFragment.arguments = bundle
        markerInfoWindowFragment.markerEditAreaClickListener = this

        val customMarkerInfoWindow = InfoWindow(marker, markerSpec, markerInfoWindowFragment)
        infoWindowList.add(customMarkerInfoWindow)

        mMap.setOnMarkerClickListener(this)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }

    override fun onMarkerEditAreaClick(result: CommunityAreaListResponse.Result) {
        showAreaAddDialog(result)
    }

    private fun drawCircleOnMap(
        latLng: LatLng,
        radius: Double,
        color: Int
    ) {
        val radiusInMeters = radius
        val colors = ColorUtils.setAlphaComponent(color, 30)

        val circleOptions: CircleOptions =
            CircleOptions().center(latLng).radius(radiusInMeters)
                .fillColor(colors)
                .strokeColor(colors)
                .strokeWidth(2f)
        mMap.addCircle(circleOptions)
    }

    override fun onMarkerClick(v: Marker?): Boolean {
        // v!!.showInfoWindow()
        val customMarkerInfoWindow = infoWindowList[v!!.zIndex.toInt()]
        infoWindowManager!!.toggle(customMarkerInfoWindow, true)
        return true
    }

    private val addCommAreaResponseObserver = Observer<AddCommunityResponse> {
        CommunityActivity.IS_REFRESH = true
        finish()
        startActivity(intent)
    }

    private fun addReminder(
        reminder: Reminder,
        list: ArrayList<CommunityAreaListResponse.Result?>
    ) {
        getRepository().add(reminder,
            success = {
                if (coutAddDataGeoFanching != list.size) {
                    for (i in coutAddDataGeoFanching until list.size) {
                        val reminder1 = Reminder(latLng = null, radius = null, message = null)
                        coutAddDataGeoFanching += 1
                        reminder1.latLng = LatLng(
                            list[i]!!.areaLatitude!!.toDouble(),
                            list[i]!!.areaLongitude!!.toDouble()
                        )
                        reminder1.message = list[i]!!.areaName
                        reminder1.radius = list[i]!!.areaRange!!.toDouble() * 1000
                        addReminder(reminder1, list)
                        break
                    }
                } else {
                    showReminders()
                }
            },
            failure = {
                AppUtils.showSnackBar(binding.root, it)
            })
    }

    private fun showReminders() {
        mMap.run {
            clear()
            for (reminder in getRepository().getAll()) {
                AppUtils.showReminderInMap(this@CommunityDetailsActivity, this, reminder)
            }
        }
    }

    private fun removeReminder(reminder: Reminder) {
        getRepository().remove(
            reminder,
            success = {
                Log.e("removeReminder", "success")
            },
            failure = {
                Log.e("removeReminder", "failure->" + it)
            })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        /*val markerWindowView =
            CustomMarkerInfoWindowView(
                layoutInflater
            )
        googleMap.setInfoWindowAdapter(markerWindowView)
        mMap.setOnMapClickListener(this)*/

        infoWindowManager!!.setWindowShowListener(this@CommunityDetailsActivity)
        infoWindowManager!!.setOnMapClickListener(this)

        drawWholeCommunityCircle()

        binding.mapChangeToggle.setOnClickListener {
            if (binding.mapChangeToggle.isChecked) {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            } else {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

    }

    private fun drawWholeCommunityCircle() {
        val circleOptions = CircleOptions()
            .center(
                LatLng(
                    communityData.commLatitude!!.toDouble(),
                    communityData.commLongitude!!.toDouble()
                )
            )
            .radius(communityData.commRange!!.toDouble())
            .fillColor(Color.TRANSPARENT)
            .strokeColor(Color.BLACK)
            .strokeWidth(5f)

        mMap.addCircle(circleOptions)
    }

    override fun onMapClick(latLng: LatLng?) {
        latLng?.let {
            if (clickMap) {
                marker.remove()
            }
            mLatLng = latLng
            clickMap = true
            Log.e("CLICK_LAT/LONG", "->" + latLng)
            getAddress(latLng)
        }

    }

    override fun getViewModel(): CommunityDetailsViewModel {
        mViewModel = ViewModelProvider(this).get(CommunityDetailsViewModel::class.java)
        return mViewModel
    }

    private fun initSpinnerViolationReason() {
        val commNameList: ArrayList<String> = ArrayList()
        val commIdList: ArrayList<String> = ArrayList()
        for (i in 0 until listOfCommunityResponse.result.size) {
            commNameList.add(listOfCommunityResponse.result[i]!!.commName!!)
            commIdList.add(listOfCommunityResponse.result[i]!!.commId!!.toString())
        }

        val selectCommunityPopupWindow = ListPopupWindow(this)
        selectCommunityPopupWindow.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_drop_down_item,
                commNameList
            )
        )
        selectCommunityPopupWindow.anchorView = binding.toolbarLayout.tvToolbarSpinner
        selectCommunityPopupWindow.isModal = true

        selectCommunityPopupWindow.setOnItemClickListener { _, _, position, _ ->
            /* val selectedSortBy = commNameList[position]
             coutAddDataGeoFanching = 0
             communityId = commIdList[position]
             clearGeoFanchingData()
             mViewModel.callListOfCommApi(communityId)

             binding.toolbarLayout.tvToolbarSpinner.text = selectedSortBy*/
            selectCommunityPopupWindow.dismiss()
            intent.putExtra(AppConstants.COMMUNITY_DATA, listOfCommunityResponse.result[position])
            intent.putExtra(AppConstants.COMMUNITY_LIST, listOfCommunityResponse)
            finish()
            startActivity(intent)


        }
        binding.toolbarLayout.tvToolbarSpinner.setOnClickListener { selectCommunityPopupWindow.show() }


    }

    /*get current address*/
    private fun getAddress(latLng: LatLng?) {
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        addresses = geocoder.getFromLocation(
            latLng!!.latitude,
            latLng!!.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].getLocality()
        val state = addresses[0].getAdminArea()
        val country = addresses[0].getCountryName()
        val postalCode = addresses[0].getPostalCode()
        val knownName = addresses[0].getFeatureName() // Only if available else return NULL
        val vectorToBitmap = AppUtils.vectorToBitmap(resources, R.drawable.ic_pin)
        marker =
            mMap.addMarker(MarkerOptions().position(latLng).icon(vectorToBitmap).title(address))
    }

    override fun onLocationReceived(location: Location?) {
        location?.let {
            Log.e("LATITUDE", "" + it.latitude)
            Log.e("LONGITUDE", "" + it.longitude)


            locationProvider?.stopLocationUpdates()
            // When we need to get location again, then call below line
            //locationProvider?.startGettingLocations()
        }
    }

    fun showAreaAddDialog(result: CommunityAreaListResponse.Result?) {
        val alert = AlertDialog.Builder(this)

        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.dialog_add_community, null)
        val etAreaName = alertLayout.findViewById<AppCompatEditText>(R.id.etAreaName)
        val seekBarKm = alertLayout.findViewById<AppCompatSeekBar>(R.id.seekBarKm)
        val tvMinLimit = alertLayout.findViewById<AppCompatTextView>(R.id.tvMinLimit)
        val tvMaxLimit = alertLayout.findViewById<AppCompatTextView>(R.id.tvMaxLimit)
        val btnSubmit = alertLayout.findViewById<AppCompatButton>(R.id.btnSubmit)
        var selectedKm = 0
        seekBarKm.incrementProgressBy(100)
        seekBarKm.max = communityData.commRange!!.toDouble().toInt()
        tvMaxLimit.text =
            (communityData.commRange!!.toDouble().toInt()).toString() + " Meter"
        seekBarKm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("TAG", "-->" + progress)
                selectedKm = progress
                tvMinLimit.text = "$progress Meter"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        if (result != null) {
            etAreaName.setText(result.areaName)
            seekBarKm.progress = result.areaRange!!.toDouble().toInt()
        } else {
            seekBarKm.progress = 0
        }

        alert.setView(alertLayout)
        alert.setCancelable(true)

        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        btnSubmit.setOnClickListener {
            if (result == null) {
                if (!clickMap) {
                    AppUtils.showSnackBar(btnSubmit, "Please selected location")
                    return@setOnClickListener
                }
            }
            if (etAreaName.text!!.isEmpty()) {
                AppUtils.showSnackBar(btnSubmit, "Please enter area name")
                return@setOnClickListener
            }
            if (selectedKm == 0) {
                AppUtils.showSnackBar(btnSubmit, "Please set km")
                return@setOnClickListener
            }
            if (result == null) {
                mViewModel.callAddCommApi(
                    communityId,
                    mLatLng,
                    selectedKm,
                    etAreaName.text.toString()
                )
            } else {
                mViewModel.callEditCommApi(
                    result,
                    etAreaName.text.toString(),
                    selectedKm,
                    communityId
                )
            }
            dialog.dismiss()
        }

    }

    override fun onStop() {
        super.onStop()
        locationProvider?.onStop()
    }

    override fun internetErrorRetryClicked() {
    }

    override fun onClick(v: View?) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MyLocationProvider.LOCATION_PERMISSION_REQUEST_CODE) {
            locationProvider?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyLocationProvider.REQUEST_LOCATION_SETTINGS) {
            locationProvider?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        finishActivity()
        super.onBackPressed()
    }

    private fun finishActivity() {
        val returnIntent = Intent()
        returnIntent.putExtra("result", isRefresh)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onWindowHideStarted(infoWindow: InfoWindow) {

    }

    override fun onWindowHidden(infoWindow: InfoWindow) {

    }

    override fun onWindowShown(infoWindow: InfoWindow) {

    }

    override fun onWindowShowStarted(infoWindow: InfoWindow) {

    }

}
