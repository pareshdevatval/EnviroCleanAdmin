package com.envirocleanadmin

import android.app.Application
import com.envirocleanadmin.di.component.*
import com.envirocleanadmin.di.module.AppModule
import com.envirocleanadmin.di.module.LocalDataModule
import com.envirocleanadmin.di.module.NetworkModule
import com.envirocleanadmin.geofancing.ReminderRepository


class EnviroCleanAdminApp : Application() {
    private lateinit var repository: ReminderRepository


    override fun onCreate() {
        super.onCreate()
        repository = ReminderRepository(this)
    }


    fun getAppComponent(): AppComponent {
        return DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun getLocalDataComponent(): LocalDataComponent {
        return DaggerLocalDataComponent.builder().appComponent(getAppComponent()).localDataModule(
            LocalDataModule()
        ).build()
    }

    fun getNetworkComponent(): NetworkComponent {
        return DaggerNetworkComponent.builder().appComponent(getAppComponent())
            .networkModule(NetworkModule()).build()
    }
    fun getRepository() = repository

}