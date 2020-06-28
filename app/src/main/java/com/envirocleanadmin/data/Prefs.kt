package com.envirocleanadmin.data

import android.content.Context
import android.content.SharedPreferences
import com.envirocleanadmin.data.response.LoginResponse
import com.google.gson.Gson

class Prefs(context: Context) {

    private val PREF_USER_DATA = "userData"
    private val PREF_ACCESS_TOKEN = "accessToken"
    private val PREF_DEVICE_TOKEN = "deviceToken"
    private val PREFS_REMINDERS = "REMINDERS"
    private val PREFS_FIREBASE_TOKEN = "firebse_token"

    private val DELIVERY_CHARGES = "delivery_charges"
    private val SETTINGS = "settings"

    private var sharedPreferencesRememberMe: SharedPreferences? = null
    private val SP_NAME = Prefs::class.java.name
    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sharedPreferencesRememberMe = context.getSharedPreferences(PREFS_REMINDERS, Context.MODE_PRIVATE)
    }

    var isSkipped: Boolean
        set(value) = sharedPreferences!!.edit().putBoolean("skipped", value).apply()
        get() = sharedPreferences!!.getBoolean("skipped", false)



    var isLoggedIn: Boolean
        set(value) = sharedPreferences!!.edit().putBoolean("key", value).apply()
        get() = sharedPreferences!!.getBoolean("key", false)

    var userDataModel: LoginResponse?
        get() = Gson().fromJson<LoginResponse>(sharedPreferences!!.getString(PREF_USER_DATA, ""), LoginResponse::class.java)
        set(userDataModel) = sharedPreferences!!.edit().putString(PREF_USER_DATA, Gson().toJson(userDataModel)).apply()
   /* var accessToken: String
        get() = sharedPreferences!!.getString(PREF_ACCESS_TOKEN, "")
        set(accessToken) = sharedPreferences!!.edit().putString(PREF_ACCESS_TOKEN, accessToken).apply()

    var deviceToken: String
        get() = sharedPreferences!!.getString(PREF_DEVICE_TOKEN, "")
        set(deviceToken) = sharedPreferences!!.edit().putString(PREF_DEVICE_TOKEN, deviceToken).apply()

    var reminders: List<ReminderData>?
        get() = Gson().fromJson(sharedPreferences!!.getString(PREFS_REMINDERS, ""), Array<ReminderData>::class.java)?.toList()
        set(reminders) = sharedPreferences!!.edit().putString(PREFS_REMINDERS, Gson().toJson(reminders)).apply()
*/
    fun clearPrefs() {
        sharedPreferences!!.edit().clear().apply()
    }

    companion object {
        var prefs: Prefs? = null

        fun getInstance(context: Context): Prefs? {
            prefs = if (prefs != null) prefs else Prefs(context)
            return prefs
        }
    }

    var rememberMe: Boolean
        set(value) = sharedPreferences!!.edit().putBoolean("remember_me", value).apply()
        get() = sharedPreferences!!.getBoolean("remember_me", false)

    var userName: String
        set(value) = sharedPreferences!!.edit().putString("username", value).apply()
        get() = sharedPreferences!!.getString("username", "")!!

    var password: String
        set(value) = sharedPreferences!!.edit().putString("password", value).apply()
        get() = sharedPreferences!!.getString("password", "")!!

    var stripePublicKey: String
        set(value) = sharedPreferences!!.edit().putString("stripe_publication_key", value).apply()
        get() = sharedPreferences!!.getString("stripe_publication_key", "")!!

    var stripeSecretKey: String
        set(value) = sharedPreferences!!.edit().putString("stripe_secret_key", value).apply()
        get() = sharedPreferences!!.getString("stripe_secret_key", "")!!

    var firebaseToken: String?
        get() = sharedPreferencesRememberMe!!.getString(PREFS_FIREBASE_TOKEN, "")
        set(firebaseToken) = sharedPreferencesRememberMe!!.edit().putString(PREFS_FIREBASE_TOKEN, firebaseToken!!).apply()


}