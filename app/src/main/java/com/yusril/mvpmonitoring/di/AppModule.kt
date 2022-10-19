package com.yusril.mvpmonitoring.di

import android.content.Context
import com.yusril.mvpmonitoring.BuildConfig
import com.yusril.mvpmonitoring.core.data.RepositoryImpl
import com.yusril.mvpmonitoring.core.data.local.PreferenceDataSource
import com.yusril.mvpmonitoring.core.data.remote.MonitoringApi
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.ui.detail.DetailPresenter
import com.yusril.mvpmonitoring.ui.login.LoginPresenter
import com.yusril.mvpmonitoring.ui.main.MainPresenter
import com.yusril.mvpmonitoring.ui.splashscreen.SplashScreenPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMonitoringApi(): MonitoringApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MonitoringApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(@ApplicationContext context: Context): PreferenceDataSource = PreferenceDataSource(context)

    @Singleton
    @Provides
    fun provideRepository(api: MonitoringApi, local: PreferenceDataSource): MainRepository = RepositoryImpl(api, local)


    @Singleton
    @Provides
    fun provideMainPresenter(repository: MainRepository) : MainPresenter = MainPresenter(repository)

    @Provides
    fun provideDetailPresenter(repository: MainRepository): DetailPresenter = DetailPresenter(repository)

    @Provides
    fun provideSplashScreenPresenter(repository: MainRepository): SplashScreenPresenter = SplashScreenPresenter(repository)

    @Provides
    fun provideLoginPresenter(repository: MainRepository): LoginPresenter = LoginPresenter(repository)

}