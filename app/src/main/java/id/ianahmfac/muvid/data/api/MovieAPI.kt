package id.ianahmfac.muvid.data.api

import id.ianahmfac.muvid.BuildConfig.API_KEY_MOVIE
import id.ianahmfac.muvid.data.models.detail.DetailMovieResponse
import id.ianahmfac.muvid.data.models.list.ListMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {
    @GET("3/movie/now_playing")
    suspend fun getListNowPlaying(
        @Query("page")
        page: Int,
        @Query("api_key")
        apiKey: String = API_KEY_MOVIE,
        @Query("language")
        language: String = "en-US"
    ): Response<ListMovieResponse>

    @GET("3/movie/top_rated")
    suspend fun getListTopRated(
        @Query("page")
        page: Int,
        @Query("api_key")
        apiKey: String = API_KEY_MOVIE,
        @Query("language")
        language: String = "en-US"
    ): Response<ListMovieResponse>

    @GET("3/movie/upcoming")
    suspend fun getListUpcoming(
        @Query("page")
        page: Int,
        @Query("api_key")
        apiKey: String = API_KEY_MOVIE,
        @Query("language")
        language: String = "en-US"
    ): Response<ListMovieResponse>

    @GET("3/movie/{movie_id}")
    suspend fun getDetails(
        @Path("movie_id")
        movieId: Int,
        @Query("api_key")
        apiKey: String = API_KEY_MOVIE,
        @Query("language")
        language: String = "en-US",
        @Query("append_to_response")
        response: String = "videos"
    ): Response<DetailMovieResponse>

    @GET("3/search/movie")
    suspend fun getSearch(
        @Query("query")
        query: String,
        @Query("page")
        page: Int,
        @Query("api_key")
        apiKey: String = API_KEY_MOVIE,
        @Query("language")
        language: String = "en-US"
    ): Response<ListMovieResponse>
}