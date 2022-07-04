package best.hyun.cinema.dto

data class CommentListResult(
    val message: String,
    val code: Int,
    val resultType: String,
    val totalCount: Int,
    val result: List<Comment>
)