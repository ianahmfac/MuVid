package id.ianahmfac.muvid.ui.home.fragments.bookmark

import androidx.lifecycle.ViewModel
import id.ianahmfac.muvid.data.repository.MovieRepository

class BookmarkViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    fun getBookmarkedMovies() = movieRepository.getAllMovies()
}