package com.zharkovsky.memes.services

import com.zharkovsky.memes.models.LoginUserRequestDto
import com.zharkovsky.memes.models.AuthInfoDto
import com.zharkovsky.memes.utils.Constants.AUTH_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface JSONPlaceHolderApi {

    @POST(AUTH_URL)
    fun login(@Body authBody: LoginUserRequestDto): Call<AuthInfoDto>
}
