package id.ianahmfac.muvid.ui.details

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.ianahmfac.muvid.data.models.detail.DetailMovieResponse
import id.ianahmfac.muvid.data.models.list.Result
import id.ianahmfac.muvid.data.repository.MovieRepository
import id.ianahmfac.muvid.utils.MovieApplication
import id.ianahmfac.muvid.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class DetailViewModel(private val movieRepository: MovieRepository, app: Application) :
    AndroidViewModel(app) {
    val detail: MutableLiveData<Resources<DetailMovieResponse>> = MutableLiveData()

    fun getDetail(movieId: Int) = viewModelScope.launch {
        safeDetailCall(movieId)
    }

    private fun handlerDetailResponse(response: Response<DetailMovieResponse>): Resources<DetailMovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resources.Success(it)
            }
        }
        return Resources.Error(response.message())
    }

    private suspend fun safeDetailCall(movieId: Int) {
        detail.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getDetails(movieId)
                detail.postValue(handlerDetailResponse(response))
            } else {
                detail.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> detail.postValue(Resources.Error("Network Failure"))
                else -> detail.postValue(Resources.Error("Conversion Error"))
            }
        }
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

    fun getMovie(id: Int) = movieRepository.getMovie(id)

    fun savedMovie(result: Result) = viewModelScope.launch {
        movieRepository.upsert(result)
    }

    fun deleteMovie(result: Result) = viewModelScope.launch {
        movieRepository.delete(result)
    }
}