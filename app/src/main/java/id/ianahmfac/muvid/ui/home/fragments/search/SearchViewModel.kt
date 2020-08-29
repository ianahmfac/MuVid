package id.ianahmfac.muvid.ui.home.fragments.search

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.ianahmfac.muvid.data.models.list.ListMovieResponse
import id.ianahmfac.muvid.data.repository.MovieRepository
import id.ianahmfac.muvid.utils.MovieApplication
import id.ianahmfac.muvid.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchViewModel(private val movieRepository: MovieRepository, app: Application) :
    AndroidViewModel(app) {

    val search: MutableLiveData<Resources<ListMovieResponse>> = MutableLiveData()
    private var pageSearch = 1

    fun getSearch(query: String) = viewModelScope.launch {
        safeSearchCall(query)
    }

    private suspend fun safeSearchCall(query: String) {
        search.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getSearch(query, pageSearch)
                search.postValue(handlerSearchResponse(response))
            } else {
                search.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> search.postValue(Resources.Error("Network Failure"))
                else -> search.postValue(Resources.Error("Conversion Error"))
            }
        }
    }

    private fun handlerSearchResponse(response: Response<ListMovieResponse>): Resources<ListMovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resources.Success(it)
            }
        }
        return Resources.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<MovieApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}