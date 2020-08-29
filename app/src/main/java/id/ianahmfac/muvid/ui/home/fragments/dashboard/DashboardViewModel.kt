package id.ianahmfac.muvid.ui.home.fragments.dashboard

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
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

class DashboardViewModel(private val movieRepository: MovieRepository, app: Application) :
    AndroidViewModel(app) {
    // FROM API

    val listNowPlaying: MutableLiveData<Resources<ListMovieResponse>> = MutableLiveData()
    private var pageListNowPlaying = 1
    private var nowPlayingResponse: ListMovieResponse? = null

    val listTopRated: MutableLiveData<Resources<ListMovieResponse>> = MutableLiveData()
    private var pageTopRated = 1

    init {
        getNowPlaying()
        getTopRated()
    }

    fun getNowPlaying() = viewModelScope.launch {
        safeNowPlayingCall()
    }

    private fun getTopRated() = viewModelScope.launch {
        safeTopRatedCall()
    }

    private fun handlerNowPlayingResponse(response: Response<ListMovieResponse>): Resources<ListMovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { npResponse ->
                pageListNowPlaying++
                if (nowPlayingResponse == null) {
                    nowPlayingResponse = npResponse
                } else {
                    val oldResult = nowPlayingResponse?.results
                    val newResult = npResponse.results
                    oldResult?.addAll(newResult)
                }
                return Resources.Success(nowPlayingResponse ?: npResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private fun handlerTopRatedResponse(response: Response<ListMovieResponse>): Resources<ListMovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { trResponse ->
                return Resources.Success(trResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private suspend fun safeNowPlayingCall() {
        listNowPlaying.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getListNowPlaying(pageListNowPlaying)
                listNowPlaying.postValue(handlerNowPlayingResponse(response))
            } else {
                listNowPlaying.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> listNowPlaying.postValue(Resources.Error("Network Failure"))
                else -> listNowPlaying.postValue(Resources.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeTopRatedCall() {
        listTopRated.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getListTopRated(pageTopRated)
                listTopRated.postValue(handlerTopRatedResponse(response))
            } else {
                listTopRated.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> listTopRated.postValue(Resources.Error("Network Failure"))
                else -> listTopRated.postValue(Resources.Error("Conversion Error"))
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
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}