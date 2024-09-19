package com.example.fcmapplication

import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {

    @POST("/send")
    suspend fun sendMessage(
        @Body sendMessageDto: SendMessageDto
    )

    @POST("/broadcast")
    suspend fun broadcast(
        @Body body: SendMessageDto
    )



}