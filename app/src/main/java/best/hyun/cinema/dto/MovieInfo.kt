package best.hyun.cinema.dto

data class MovieInfo(
    val message: String,
    val code: Int,
    val resultType: String,
    val result: List<Movie>
)