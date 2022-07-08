package best.hyun.cinema.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comment(
    @PrimaryKey
    val id: Int,
    val writer: String,
    val movieId: Int,
    val writer_image: String?,
    val time: String,
    val timestamp: Long,
    val rating: Float,
    val contents: String,
    val recommend: Int
)