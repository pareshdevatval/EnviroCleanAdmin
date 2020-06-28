package com.envirocleanadmin.di.module

import android.content.Context
import android.util.Log
import com.envirocleanadmin.BuildConfig
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.utils.AppUtils
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit


//Created by imobdev-rujul on 7/1/19
@Module
class NetworkModule {

    /**
     * Provides the Api service implementation.
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the Api service implementation.
     */
    @Provides
    fun provideApiClient(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Provides the Retrofit object.
     * @return the Retrofit object
     */
    @Provides
    fun provideRetrofitInterface(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(httpClient)
                .build()
    }

    /**
     * Provides the Http client object.
     * @return the Http client object
     */
    @Provides
    fun provideHttpClient(context: Context): OkHttpClient {
        val TIMEOUT = 30 // Keeping timeout of 30 seconds

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(Interceptor { chain ->

                    val original = chain.request()
                    val requestBuilder = original.newBuilder().header("Authorization", "")
                    requestBuilder.header("Accept", "application/json")
                    requestBuilder.method(original.method, original.body)
                    val prefs = Prefs.getInstance(context)
                    prefs?.let {
                        if(it.isLoggedIn){
                            val user = it.userDataModel
                            user?.let {
                                requestBuilder.header("Authorization", "Bearer "+ it.result?.token)
                            }
                        }
                    }
                    requestBuilder.header("Accept-Language", "en")
                    requestBuilder.header("appaccesstoken", BuildConfig.appaccesstoken)
                    val request = requestBuilder.build()
                    val response = chain.proceed(request)

                    if (response.isSuccessful) {
                        val data = response.body!!.string()
                        Log.e("RESPONSE = ", data)
                        return@Interceptor response.newBuilder()
                                .body(ResponseBody.create(response.body!!.contentType(), data)).build()
                    } else if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        AppUtils.logoutUser(context,prefs)
                    }
                    return@Interceptor response
                })
                .build()
    }


}