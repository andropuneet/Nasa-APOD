package org.nasa_apod.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.nasa_apod.BuildConfig
import org.nasa_apod.api.ApiInterface
import org.nasa_apod.db.ImageDao
import org.nasa_apod.db.ImagiaryDb
import org.nasa_apod.utils.Constants
import org.nasa_apod.utils.LiveDataCallAdapterFactory
import org.nasa_apod.utils.LiveNetworkMonitor
import org.nasa_apod.utils.NetworkMonitor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    @Named
    fun provideBaseUrlString(): String {
        return Constants.BASE_URL
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): ImagiaryDb {
        return Room.databaseBuilder(app, ImagiaryDb::class.java, "imagiary.db").build()
    }

    @Singleton
    @Provides
    fun provideImageDao(db: ImagiaryDb): ImageDao {
        return db.imageDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(application: Application): NetworkMonitor {
        return LiveNetworkMonitor(application)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache
    ): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE)
        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)
        httpClient.addInterceptor(logging)
        httpClient.readTimeout(180, TimeUnit.SECONDS)
        httpClient.connectTimeout(180, TimeUnit.SECONDS)
        httpClient.writeTimeout(180, TimeUnit.SECONDS)
        return httpClient
    }

    @Provides
    @Singleton
    fun provideMoshiConverter(): Converter.Factory {
        val moshi = Moshi.Builder()
            .build()
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        converter: Converter.Factory,
        @Named baseUrl: String,
        okHttpClient: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converter)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}