package best.hyun.cinema.dto

data class MovieListResult(
    val message: String,
    val code: Int,
    val resultType: String,
    val result: List<Movie>
)