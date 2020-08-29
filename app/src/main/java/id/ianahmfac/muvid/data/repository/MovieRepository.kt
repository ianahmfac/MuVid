package id.ianahmfac.muvid.data.repository

import id.ianahmfac.muvid.data.api.RetrofitInstance
import id.ianahmfac.muvid.data.db.MovieDatabase
import id.ianahmfac.muvid.data.models.list.Result

class MovieRepository(private val database: MovieDatabase) {
    suspend fun getListNowPlaying(pageNumber: Int) =
        RetrofitInstance.api.getListNowPlaying(pageNumber)

    suspend fun getListTopRated(pageNumber: Int) = RetrofitInstance.api.getListTopRated(pageNumber)

    suspend fun getListUpcoming(pageNumber: Int) = RetrofitInstance.api.getListUpcoming(pageNumber)

    suspend fun getDetails(movieId: Int) = RetrofitInstance.api.getDetails(movieId)

    suspend fun getSearch(query: String, pageNumber: Int) =
        RetrofitInstance.api.getSearch(query, pageNumber)

    // From ROOM DB
    fun getAllMovies() = database.movieDao().getAllMovies()

    fun getMovie(id: Int) = database.movieDao().getMovie(id)

    suspend fun upsert(result: Result) {
        result.bookmarked = true
        database.movieDao().upsert(result)
    }

    suspend fun delete(result: Result) {
        database.movieDao().delete(result)
    }
}