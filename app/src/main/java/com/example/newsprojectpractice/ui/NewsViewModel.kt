package com.example.newsprojectpractice.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsprojectpractice.models.Article
import com.example.newsprojectpractice.models.NewsResponse
import com.example.newsprojectpractice.repository.NewsRepository
import com.example.newsprojectpractice.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

// ViewModel untuk mengelola data berita dan koneksi internet dalam aplikasi berita
class NewsViewModel(app: Application, val newsRepository: NewsRepository): AndroidViewModel(app) {

    // LiveData untuk menyimpan dan mengelola status berita utama (headlines)
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsResponse? = null

    // LiveData untuk menyimpan dan mengelola status pencarian berita
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    // Inisialisasi untuk mendapatkan berita utama saat aplikasi dimulai
    init {
        getHeadLines("us")
    }

    // Fungsi untuk mengambil berita utama berdasarkan kode negara
    fun getHeadLines(countryCode: String) = viewModelScope.launch {
        headLinesInternet(countryCode)
    }

    // Fungsi untuk melakukan pencarian berita berdasarkan query pencarian
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    // Menangani respons berita utama (headlines) dan menggabungkan artikel-artikel baru dengan yang lama
    private fun handleHeadLinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                headlinesPage++
                if (headlinesResponse == null) {
                    headlinesResponse = resultResponse
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // Menangani respons pencarian berita dan menggabungkan artikel-artikel baru dengan yang lama
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // Fungsi untuk menambahkan artikel ke dalam favorit
    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    // Fungsi untuk mengambil daftar berita favorit
    fun getFavouritesNews() = newsRepository.getFavouriteNews()

    // Fungsi untuk menghapus artikel dari favorit
    fun deleteArticles(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    // Fungsi untuk mengecek koneksi internet
    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    // Fungsi untuk mengambil berita utama dari internet dengan penanganan koneksi
    private suspend fun headLinesInternet(countryCode: String) {
        headlines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadLinesResponse(response))
            } else {
                headlines.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No signal"))
            }
        }
    }

    // Fungsi untuk melakukan pencarian berita dari internet dengan penanganan koneksi
    private suspend fun searchNewsInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("no signal"))
            }
        }
    }
}
