package id.ianahmfac.muvid.data.models.list

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "movies")
data class Result(
    @PrimaryKey
    val id: Int,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val vote_average: Double?,
    var bookmarked: Boolean = false
) : Serializable {
    fun getPosterPath(): String = "https://image.tmdb.org/t/p/w500$poster_path"

    fun getReleaseDate(): String = formatDate(release_date)

    private fun formatDate(date: String?): String {
        return if (date == null || date == "") {
            ""
        } else {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            formatter.format(parser.parse(date) as Date)
        }
    }
}