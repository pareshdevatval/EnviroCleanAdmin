package com.envirocleanadmin.di.module

import android.app.Application
import android.content.Context
import com.envirocleanadmin.EnviroCleanAdminApp
import dagger.Module
import dagger.Provides

@Module
class AppModule constructor(val application: EnviroCleanAdminApp) {


    @Provides
    fun provideApplication(): Application = application

    /**
     * Provides the Application context.
     * @return the Application context
     */
    @Provides
    fun provideContext(): Context = application.applicationContext

}