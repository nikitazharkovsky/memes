package com.zharkovsky.memes.services

import com.zharkovsky.memes.utils.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor() {
    companion object {
        private var instance: NetworkService? = null

        fun getInstance(): NetworkService {
            if (instance == null) {
                instance = NetworkService()
            }
            return instance!!
        }
    }

    private val retrofit: Retrofit

    val jsonApi: JSONPlaceHolderApi
        get() = retrofit.create(JSONPlaceHolderApi::class.java)

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}
