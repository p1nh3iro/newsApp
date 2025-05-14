package com.example.newsapplication.network

import com.example.newsapplication.BuildConfig
import com.example.newsapplication.model.TopHeadlines
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        // TODO STORY 4
        @Query("sources") source: String = BuildConfig.NEWS_SOURCE,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY,
    ): TopHeadlines
}
