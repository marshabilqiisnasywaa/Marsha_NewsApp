package com.example.newsprojectpractice.repository

import com.example.newsprojectpractice.api.RetrofitInstance
import com.example.newsprojectpractice.db.ArticleDatabase
import com.example.newsprojectpractice.models.Article

class NewsRepository(private val db: ArticleDatabase) {
    suspend fun getHeadlines (countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines (countryCode, pageNumber)

    suspend fun searchNews (searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews (searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}