package id.ianahmfac.muvid.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ianahmfac.muvid.data.repository.MovieRepository
import id.ianahmfac.muvid.ui.details.DetailViewModel
import id.ianahmfac.muvid.ui.home.fragments.bookmark.BookmarkViewModel
import id.ianahmfac.muvid.ui.home.fragments.dashboard.DashboardViewModel
import id.ianahmfac.muvid.ui.home.fragments.search.SearchViewModel
import id.ianahmfac.muvid.ui.home.fragments.upcoming.UpcomingViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val movieRepository: MovieRepository, val app: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(movieRepository, app) as T
            }
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> {
                UpcomingViewModel(movieRepository, app) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(
                    movieRepository,
                    app
                ) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(movieRepository, app) as T
            }
            modelClass.isAssignableFrom(BookmarkViewModel::class.java) -> {
                BookmarkViewModel(movieRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel Class: ${modelClass.name}")
        }
    }
}