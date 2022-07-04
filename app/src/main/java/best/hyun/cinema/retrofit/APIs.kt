package best.hyun.cinema.retrofit

import best.hyun.cinema.dto.*
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.*

interface MovieListAPI {
    @GET("movie/readMovieList")
    fun readMovieList(): Call<MovieListResult>
}

interface MovieInfoAPI {
    @GET("/movie/readMovie")
    fun readMovie(@Query("id") id: Int): Call<MovieInfo>
}

interface CommentListAPI {
    @GET("/movie/readCommentList")
    fun readComment(@Query("id") id: Int, @Query("limit") limit: Int?): Call<CommentListResult>
}

interface CommentWriteAPI {
    @FormUrlEncoded
    @POST("/movie/createComment")
    fun writeComment(
        @Field("id") id: Int,
        @Field("writer") writer: String,
        @Field("rating") rating: Float,
        @Field("contents") contents: String
    ): Call<SimpleStatus>
}

interface LikeDisLikeAPI {
    @FormUrlEncoded
    @POST("/movie/increaseLikeDisLike")
    fun increaseLikeDisLike(
        @Field("id") id: Int,
        @Field("likeyn") like: String?,
        @Field("dislikeyn") dislike: String?
    ): Call<SimpleStatus>
}