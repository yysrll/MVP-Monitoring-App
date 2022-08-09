package com.yusril.mvvmmonitoring.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yusril.mvvmmonitoring.BuildConfig
import com.yusril.mvvmmonitoring.core.data.RepositoryImpl
import com.yusril.mvvmmonitoring.core.data.local.PreferenceDataSource
import com.yusril.mvvmmonitoring.core.data.remote.MonitoringApi
import com.yusril.mvvmmonitoring.core.domain.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMonitoringApi(): MonitoringApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MonitoringApi::class.java)

    @Singleton
    @Provides
    fun provideLocalDataSource(@ApplicationContext context: Context): PreferenceDataSource = PreferenceDataSource(context)

    @Singleton
    @Provides
    fun provideRepository(api: MonitoringApi, local: PreferenceDataSource): MainRepository = RepositoryImpl(api, local)

}