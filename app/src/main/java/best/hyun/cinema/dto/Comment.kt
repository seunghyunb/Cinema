package best.hyun.cinema.dto

data class Comment(
    val id: Int,
    val writer: String,
    val movieId: Int,
    val writer_imageval : String?,
    val time: String,
    val timestamp: Long,
    val rating: Float,
    val contents: String,
    val recommend: Int
)