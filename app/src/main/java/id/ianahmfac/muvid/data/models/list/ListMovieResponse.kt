package id.ianahmfac.muvid.data.models.list

data class ListMovieResponse(
    val page: Int,
    val results: MutableList<Result>,
    val total_pages: Int,
    val total_results: Int
)