package best.hyun.cinema.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import best.hyun.cinema.dto.Movie

@Dao
interface MovieDao {
    // 데이터 넣는데 매번 넣을 수는 없잖아
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieList(movies: List<Movie>)

    @Query("SELECT * FROM Movie")
    fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM Movie WHERE id=:id")
    fun getMovie(id: Int): Movie
}