package com.elts.api


import com.elts.models.SendOtpRequest
import com.elts.models.SendOtpResponse
import com.elts.models.VerifyOtpRequest
import com.elts.models.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/api/otp/send")
    fun sendOtp(@Body request: SendOtpRequest): Call<SendOtpResponse>

    @POST("/api/otp/verify")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<VerifyOtpResponse>
}



