package dev.atick.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(
    includes = [
        OkHttpClientModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val BASE_URL = "http://18.222.190.174:8080"

    @Singleton
    @Provides
    fun provideRetrofitClient(
        converterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }

}