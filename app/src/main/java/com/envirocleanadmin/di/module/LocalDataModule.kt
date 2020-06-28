package com.envirocleanadmin.di.module

import android.content.Context
import androidx.room.Room
import com.envirocleanadmin.data.Prefs
import com.envirocleanadmin.data.db.AppDatabase
import com.envirocleanadmin.utils.AppConstants

import dagger.Module
import dagger.Provides

//Created by imobdev-rujul on 8/1/19
@Module
class LocalDataModule {
    /**
     * Provides the database name.
     * @return the database name
     */
    @Provides
    fun provideDatabaseName(): String = AppConstants.DB_NAME

    /**
     * Provides the AppDatabase instance.
     * @param dbName the db name used to instantiate the AppDatabase
     * @param context the application context
     * @return the Post service implementation.
     */
    @Provides
    fun provideAppDatabase(dbName: String, context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the preferences.
     * @param context the context used to instantiate the preference
     * @return the preference object.
     */
    @Provides
    fun providePrefs(context: Context): Prefs = Prefs.getInstance(context)!!
}