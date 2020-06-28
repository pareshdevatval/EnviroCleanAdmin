package com.envirocleanadmin.di.component

import android.app.Application
import android.content.Context
import com.envirocleanadmin.di.module.AppModule
import dagger.Component


@Component(modules = [AppModule::class])
interface AppComponent {

    fun getApplication(): Application

    fun getContext(): Context

}
