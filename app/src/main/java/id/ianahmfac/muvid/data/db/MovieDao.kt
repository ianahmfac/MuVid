package id.ianahmfac.muvid.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import id.ianahmfac.muvid.data.models.list.Result

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): LiveData<List<Result>>

    @Query("SELECT * FROM movies WHERE id= :id")
    fun getMovie(id: Int): LiveData<Result>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(result: Result): Long

    @Delete
    suspend fun delete(result: Result)
}