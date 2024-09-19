package com.example.fcmapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ChatViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            Firebase.messaging.subscribeToTopic("chat").await()
        }
    }

    var state by mutableStateOf(ChatState())
        private set

    private val api: FcmApi = Retrofit.Builder()
        .baseUrl("http://192.168.29.247:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    fun onRemoteTokenChange(newToken: String) {
        state = state.copy(
            remoteToken = newToken
        )
    }

    fun onSubmitRemoteToken() {
        state = state.copy(
            isEnteringToken = false
        )
    }

    fun onMessageChange(message: String) {
        state = state.copy(
            messageText = message
        )
    }

    fun sendMessage(isBroadCast: Boolean) {

        viewModelScope.launch {

            val messageDto = SendMessageDto(
                to = if (isBroadCast) null else state.remoteToken,
                notification = NotificationBody(
                    title = "New Message",
                    body = state.messageText
                )
            )

            try {
                if (isBroadCast) {
                    api.broadcast(messageDto)
                }
                else {
                    api.sendMessage(messageDto)
                }
                state = state.copy(
                    messageText = ""
                )
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

}