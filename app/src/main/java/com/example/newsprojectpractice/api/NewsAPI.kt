package com.example.newsprojectpractice.api

import retrofit2.http.Query
import com.example.newsprojectpractice.models.NewsResponse
import com.example.newsprojectpractice.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET

interface NewsAPI {
   @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ): Response<NewsResponse>
}