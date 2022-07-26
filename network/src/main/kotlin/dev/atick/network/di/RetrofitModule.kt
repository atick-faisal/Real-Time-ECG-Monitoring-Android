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

    private const val BASE_URL = "https://ab76-119-148-3-101.ngrok.io"

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