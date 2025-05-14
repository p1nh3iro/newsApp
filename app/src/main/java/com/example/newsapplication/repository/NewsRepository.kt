package com.example.newsapplication.repository

import com.example.newsapplication.model.Article
import com.example.newsapplication.network.NewsApiService


open class NewsRepository(private val api: NewsApiService) {
    open suspend fun getTopHeadlines(): List<Article> {
        val response = api.getTopHeadlines()
        if (response.status == OK_STATUS) {
            return response.articles
        } else {
            throw Exception("API error: ${response.status}")
        }
    }

    companion object {
        private const val OK_STATUS = "ok"
    }
}