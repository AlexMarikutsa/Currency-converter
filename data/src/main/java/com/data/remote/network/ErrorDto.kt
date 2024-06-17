package com.data.remote.network

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

@JsonAdapter(ErrorDtoDeserializer::class)
data class ErrorDto(
    @SerializedName("statusCode")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: String?,
)