package id.ianahmfac.muvid.data.models.detail

data class DetailMovieResponse(
    val id: Int,
    val backdrop_path: String,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val imdb_id: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: String,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String?,
    val title: String,
    val videos: Videos,
    val vote_average: Double,
    val vote_count: Int
) {
    fun getBackdropPath(): String = "https://image.tmdb.org/t/p/w500$backdrop_path"
    fun getPosterPath(): String = "https://image.tmdb.org/t/p/w500$poster_path"
}