package id.ianahmfac.muvid.ui.home.fragments.upcoming

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

class UpcomingViewModel(private val movieRepository: MovieRepository, app: Application) :
    AndroidViewModel(app) {

    val listUpcoming: MutableLiveData<Resources<ListMovieResponse>> = MutableLiveData()
    private var pageUpcoming = 1
    private var upcomingResponse: ListMovieResponse? = null

    init {
        getUpcoming()
    }

    fun getUpcoming() = viewModelScope.launch {
        safeUpComingCall()
    }

    private suspend fun safeUpComingCall() {
        listUpcoming.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = movieRepository.getListUpcoming(pageUpcoming)
                listUpcoming.postValue(handlerUpcomingResponse(response))
            } else {
                listUpcoming.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> listUpcoming.postValue(Resources.Error("Network Failure"))
                else -> listUpcoming.postValue(Resources.Error("Conversion Error"))
            }
        }
    }

    private fun handlerUpcomingResponse(response: Response<ListMovieResponse>): Resources<ListMovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                pageUpcoming++
                if (upcomingResponse == null) {
                    upcomingResponse = it
                } else {
                    val oldResult = upcomingResponse?.results
                    val newResult = it.results
                    oldResult?.addAll(newResult)
                }
                return Resources.Success(upcomingResponse ?: it)
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