package `in`.dunder.celeris.utils

import `in`.dunder.celeris.frontend.LoginRequest
import `in`.dunder.celeris.frontend.LoginResponse
import `in`.dunder.celeris.frontend.SignupRequest
import `in`.dunder.celeris.frontend.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
