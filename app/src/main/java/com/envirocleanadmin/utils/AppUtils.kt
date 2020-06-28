package com.envirocleanadmin.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.envirocleanadmin.BuildConfig
import com.envirocleanadmin.R
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.geofancing.Reminder
import com.envirocleanadmin.ui.activity.SplashActivity
import com.georeminder.utils.filePick.FileUri
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException

/**
 * Created by Bhargav Thanki on 19/12/18.
 */
object AppUtils {

    /**
     * A method which returns the state of internet connectivity of user's phone.
     */
    fun hasInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun logE(msg: String, title: String = "ENVIRO_CLEAN") {
        Log.e(title, msg)
    }

    /**
     * A common method used in whole application to show a snack bar
     */
    fun showSnackBar(v: View, msg: String) {
        val mSnackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
        val view = mSnackBar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        //  params.gravity = Gravity.TOP
        view.layoutParams = params
        view.setBackgroundColor(ContextCompat.getColor(v.context, R.color.colorPrimaryDark))
        val mainTextView =
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        mainTextView.setTextColor(Color.WHITE)
        //mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, v.context.resources.getDimension(R.dimen.medium))
        mainTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        mainTextView.maxLines = 4
        mainTextView.gravity = Gravity.CENTER_HORIZONTAL
        mSnackBar.show()
    }

    /**
     * A common method to logout the user and redirect to login screen
     */
    fun logoutUser(
        context: Context?,
        prefs: Prefs?
    ) {

    }


    /**
     * A method to show device keyboard for user input
     */
    fun showKeyboard(activity: Activity?, view: EditText) {
        try {
            val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            Log.e("Exception showKeyboard", e.toString())
        }
    }

    /**
     * A method to hide the device's keyboard
     */
    fun hideKeyboard(activity: Activity) {
        if (activity.currentFocus != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, HIDE_IMPLICIT_ONLY)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun loadImages(
        context: Context,
        url: String,
        imageView: AppCompatImageView,
        placeHolderResource: Int
    ) {
        Glide.with(context)
            .load(BuildConfig.IMAGE_BASE_URL + url)
            .placeholder(placeHolderResource)
            .error(placeHolderResource)
            .into(imageView)
    }


    fun getScreenHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }


    private fun getAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DB_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * This method is for making new jpg file.
     * @param activity Instance of activity
     * @param prefix file name prefix
     */
    fun createImageFile(activity: Activity, prefix: String): FileUri? {
        val fileUri = FileUri()

        var image: File? = null
        try {
            image = File.createTempFile(
                prefix + System.currentTimeMillis().toString(),
                ".jpg",
                getWorkingDirectory()
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (image != null) {
            fileUri.file = image
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri.imageUrl =
                    (FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        image
                    ))
            } else {
                fileUri.imageUrl = (Uri.parse("file:" + image.absolutePath))
            }
        }
        return fileUri
    }

    /**
     * A method to get the external directory of phone, to store the image file
     */
    fun getWorkingDirectory(): File {
        val directory = File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID)
        if (!directory.exists()) {
            directory.mkdir()
        }
        return directory
    }

    fun startFromRightToLeft(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.trans_left_in,
            R.anim.trans_left_out
        )
    }

    fun EditText.requestFocusWithKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (!hasFocus()) {
            requestFocus()
        }

        post { imm.showSoftInput(this, InputMethodManager.SHOW_FORCED) }
    }

    fun hideKeyboard(context: Context, view: View) {
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun vectorToBitmap(resources: Resources, @DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun showReminderInMap(
        context: Context,
        map: GoogleMap,
        reminder: Reminder
    ) {
        if (reminder.latLng != null) {
            val latLng = reminder.latLng as LatLng
            val vectorToBitmap = vectorToBitmap(context.resources, R.drawable.ic_pin)
            val marker =
                map.addMarker(MarkerOptions().position(latLng).icon(vectorToBitmap).title(reminder.message))
            marker.tag = reminder.id
            if (reminder.radius != null) {
                val radius = reminder.radius as Double
                map.addCircle(
                    CircleOptions()
                        .center(reminder.latLng)
                        .radius(radius)

                        .strokeColor(ContextCompat.getColor(context, R.color.blue_15))
                        .fillColor(ContextCompat.getColor(context, R.color.blue_15))
                )
            }
        }
    }

    private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

    fun sendNotification(context: Context, message: String, latLng: LatLng) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
        ) {
            val name = context.getString(R.string.app_name)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val intent = SplashActivity.newInstance(context.applicationContext/*, latLng*/)

        val stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(SplashActivity::class.java)
            .addNextIntent(intent)
        val notificationPendingIntent = stackBuilder
            .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getUniqueId(), notification)
    }

    private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())

    fun convertKmsToMiter(kms: Double): Double {
        val miter = kms * 1000
        return miter;
    }
}