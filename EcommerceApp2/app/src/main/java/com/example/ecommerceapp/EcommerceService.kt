package com.example.ecommerceapp

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EcommerceService {
    @POST("api/accounts")
    suspend fun createAccount(@Body accountRequest: AccountRequest): Response<Void>

    @POST("api/accounts/login")
    suspend fun login(@Body accountRequest: AccountRequest): Response<Void>

    @PUT("/api/accounts/approve")
    suspend fun approveAccount(@Path("id") id: String): Response<Void>

    @PUT("/api/accounts/{id}/reactivate")
    suspend fun reactivateAccount(@Path("id") id: String): Response<Void>

    // Reject account
    @DELETE("/api/accounts/{id}/reject")
    suspend fun rejectAccount(@Path("id") id: String): Response<Void>

    @PUT("/api/accounts/{id}/deactivate")
    suspend fun deactivateAccount(@Path("id") id: String): Response<Void>



    @POST("api/feedback")
    suspend fun submitFeedback(@Body feedback: Feedback): Response<Void>
}

