package com.envirocleanadmin.di.component

import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.di.module.LocalDataModule
import dagger.Component

//Created by imobdev-rujul on 8/1/19
@Component(modules = [LocalDataModule::class], dependencies = [AppComponent::class])
interface LocalDataComponent {

    fun getPref(): Prefs

    fun getDatabase(): AppDatabase
/*
    fun getApplication(): Application

    fun getContext(): Context*/
}