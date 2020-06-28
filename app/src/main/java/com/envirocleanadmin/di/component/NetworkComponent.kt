package com.envirocleanadmin.di.component

import android.app.Application
import android.content.Context
import com.envirocleanadmin.data.ApiService
import com.envirocleanadmin.di.module.NetworkModule
import dagger.Component

//Created by imobdev-rujul on 7/1/19
@Component(modules = [NetworkModule::class], dependencies = [AppComponent::class])
interface NetworkComponent {

    fun getApiClient(): ApiService

    fun getApplication(): Application

    fun getContext(): Context

}