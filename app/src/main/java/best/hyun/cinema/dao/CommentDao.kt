package best.hyun.cinema.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import best.hyun.cinema.dto.Comment
import best.hyun.cinema.dto.Movie

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentList(comments: List<Comment>)

    @Query("SELECT * FROM Comment WHERE movieId=:movieId ORDER BY id DESC")
    fun getAllComments(movieId: Int): List<Comment>

    @Query("SELECT * FROM Comment WHERE movieId=:movieId ORDER BY id DESC LIMIT 2" )
    fun getComments(movieId: Int): List<Comment>
}