package best.hyun.cinema

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import best.hyun.cinema.dto.Comment
import best.hyun.cinema.dto.Movie
import best.hyun.cinema.dto.SimpleStatus
import best.hyun.cinema.retrofit.LikeDisLikeAPI
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MovieInfoFragment : Fragment() {

    companion object {
        fun newInstance(movie: Movie) = MovieInfoFragment().apply {
            // arguments는 onCreate()에서 사용한다.
            arguments = Bundle().apply {
                putParcelable("movie", movie)
            }
        }
    }

    private val TAG = "MainActivity.LikeStatus"
    private var movie: Movie? = null
    private var fta: FragmentToActivity? = null

    private lateinit var listView: ListView
    private lateinit var reviewAdapter: ReviewAdapter


    private val likeStatus = LikeStatus()
    private lateinit var thumbupImg: ImageView
    private lateinit var thumbdownImg: ImageView
    private lateinit var thumbupCount: TextView
    private lateinit var thumbdownCount: TextView
    private lateinit var showReviewListBtn: Button
    private lateinit var writeReviewBtn: Button

    // Fragment 에서 getContext 나 getActivity 가 null을 반환하는 이유는 Fragment가 아직 액티비티에 올라가지 않거나 분리되어 null을 반환하는 것이다.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fta = activity as? FragmentToActivity
    }

    override fun onDetach() {
        super.onDetach()
        if (fta != null) {
            fta = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            movie = requireArguments().getParcelable<Movie>("movie")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_info, container, false)

        thumbupImg = view.findViewById(R.id.img_thumbup)
        thumbdownImg = view.findViewById(R.id.img_thumbdown)
        thumbupCount = view.findViewById(R.id.text_thumbup)
        thumbdownCount = view.findViewById(R.id.text_thumbdown)
        showReviewListBtn = view.findViewById(R.id.btn_show_reviewlist)
        writeReviewBtn = view.findViewById(R.id.btn_review_write)

        val poster = view.findViewById<ImageView>(R.id.img_poster)
        val title = view.findViewById<TextView>(R.id.text_title)
        val gradeImage = view.findViewById<ImageView>(R.id.img_grade)
        val openingDay = view.findViewById<TextView>(R.id.text_opening_day)
        val genre = view.findViewById<TextView>(R.id.text_ganre)
        val synopsis = view.findViewById<TextView>(R.id.text_synopsis)
        val directorName = view.findViewById<TextView>(R.id.text_director_name)
        val actorName = view.findViewById<TextView>(R.id.text_actorr_name)
        val reservationGrade = view.findViewById<TextView>(R.id.text_reservation_grade)
        val reservationRate = view.findViewById<TextView>(R.id.text_reservation_rate)
        val audienceRating = view.findViewById<TextView>(R.id.text_audience_rating)
        val audience = view.findViewById<TextView>(R.id.text_audience)
        val rtbar = view.findViewById<RatingBar>(R.id.rtbar_info)
        if (movie != null) {
            Picasso.get().load(movie!!.image).into(poster)
            title.text = movie!!.title
            setGradeImage(movie!!.grade, gradeImage)
            openingDay.text = "${movie!!.date.replace('-', '.')} 개봉"
            genre.text = "${movie!!.genre}/${movie!!.duration}분"
            thumbupCount.text = "${movie!!.like}"
            thumbdownCount.text = "${movie!!.dislike}"
            synopsis.text = movie!!.synopsis
            directorName.text = movie!!.director
            actorName.text = movie!!.actor
            reservationGrade.text = "${movie!!.reservation_grade}위"
            reservationRate.text = "${movie!!.reservation_rate}%"
            audienceRating.text = "${String.format("%.2f", movie!!.audience_rating)}"
            audience.text = "${DecimalFormat("#,###").format(movie!!.audience)}명"
            rtbar.rating = (movie!!.audience_rating) / 2

            // 한줄평 2개 가져오기 (1)
            fta?.requestComment(movie!!.id, 2, movie!!.title)

            // 한줄평 모두 보기 (1)
            showReviewListBtn.setOnClickListener {
                fta?.let {
                    val bundle = Bundle().apply {
                        putInt("id", movie!!.id)
                        putString("title", movie!!.title)
                        putInt("grade", movie!!.grade)
                        putFloat("audienceRating", movie!!.audience_rating)
                    }

                    // 한줄평 모두 보기(2)
                    it.moveActivity("best.hyun.cinema.ReviewListActivity", bundle)
                }
            }

            // 한줄평 작성하기 (1)
            writeReviewBtn.setOnClickListener {
                fta?.let {
                    val bundle = Bundle().apply {
                        putInt("id", movie!!.id)
                        putString("title", movie!!.title)
                        putInt("grade", movie!!.grade)
                    }
                    it.moveActivity("best.hyun.cinema.ReviewWriteActivity", bundle)
                }
            }

            thumbupImg.setOnClickListener {

                val retrofit = Retrofit.Builder()
                    .baseUrl(resources.getString(R.string.baseUrl))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val likeDislikeAPI = retrofit.create(LikeDisLikeAPI::class.java)
                val call = likeDislikeAPI.increaseLikeDisLike(movie!!.id, "Y", null)
                call.enqueue(
                    object : Callback<SimpleStatus> {
                        override fun onResponse(
                            call: Call<SimpleStatus>,
                            response: Response<SimpleStatus>
                        ) {
                            if (!response.isSuccessful)
                                return

                            thumbupCount.text = "${movie!!.like?.plus(1)}"
                            thumbupImg.setImageResource(R.drawable.ic_thumb_up_selected)
                        }

                        override fun onFailure(call: Call<SimpleStatus>, t: Throwable) {}
                    }
                )
            }

            thumbdownImg.setOnClickListener {
                val retrofit = Retrofit.Builder()
                    .baseUrl(resources.getString(R.string.baseUrl))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val likeDislikeAPI = retrofit.create(LikeDisLikeAPI::class.java)
                val call = likeDislikeAPI.increaseLikeDisLike(movie!!.id, null, "Y")
                call.enqueue(
                    object : Callback<SimpleStatus> {
                        override fun onResponse(
                            call: Call<SimpleStatus>,
                            response: Response<SimpleStatus>
                        ) {
                            if (!response.isSuccessful)
                                return

                            thumbdownCount.text = "${movie!!.dislike?.plus(1)}"
                            thumbdownImg.setImageResource(R.drawable.ic_thumb_down_selected)
                        }

                        override fun onFailure(call: Call<SimpleStatus>, t: Throwable) {}
                    }
                )
            }
        }


        listView = view.findViewById(R.id.listview_review)
        reviewAdapter = ReviewAdapter()
        listView.adapter = reviewAdapter

        return view
    }

    private fun setGradeImage(grade: Int, img: ImageView) {
        when (grade) {
            12 -> img.setImageResource(R.drawable.ic_12)
            15 -> img.setImageResource(R.drawable.ic_15)
            19 -> img.setImageResource(R.drawable.ic_19)
            else -> img.setImageResource(R.drawable.ic_all)
        }
    }

    // 한줄평 2개 가져오기 (3)
    fun addReview(comment: Comment) {
        // 한줄평 2개 가져오기 (4) 끝
        reviewAdapter.addItem(comment)
    }

    inner class ReviewAdapter : BaseAdapter() {
        private val items = ArrayList<Comment>()

        // 한줄평 2개 가져오기 (4) 끝
        fun addItem(item: Comment) {
            items.add(item)
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val view = ReviewItem(requireContext())

            val comment = items[position]

            val userName = view.findViewById<TextView>(R.id.text_username)
            userName.text = comment.writer

            val reviewTime = view.findViewById<TextView>(R.id.text_review_time)
            setTimeStamp(reviewTime, comment.timestamp)

            val reviewContent = view.findViewById<TextView>(R.id.text_review_content)
            reviewContent.text = comment.contents

            val reviewLikeCount = view.findViewById<TextView>(R.id.text_review_like_count)
            reviewLikeCount.text = "${comment.recommend}"

            val ratingBar = view.findViewById<RatingBar>(R.id.rtbar_review)
            ratingBar.rating = comment.rating

            return view
        }

        private fun setTimeStamp(textView: TextView, timestamp: Long) {
            val now: Long = Calendar.getInstance().time.time / 1000 // 초 단위로 가져오기

            // 분 / 시간 / 일 / 날짜
            val second = 1L
            val minute = second * 60L
            val hour = minute * 60L
            val day = hour * 24L
            val month = day * 30L

            val date: String = when ((now - timestamp)) {
                in 0..hour -> {
                    "${(now - timestamp) / minute}분 전"
                }
                in hour + 1..day -> {
                    "${(now - timestamp) / hour}시간 전"
                }
                in day + 1..month -> {
                    "${(now - timestamp) / day}일 전"
                }
                else -> {
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                    dateFormat.format(Date(timestamp * 1000))
                }
            }

            textView.text = date
        }
    }

    enum class LIKESTAT { LIKE, DISLIKE }
    inner class LikeStatus(
        public var up: Boolean = false,
        private var down: Boolean = false,
        private var upCount: Int = 15,
        private var downCount: Int = 1
    ) {
        fun changeStat(stat: LIKESTAT) {
            if (stat == LIKESTAT.LIKE) { // up 버튼에서 눌렀을 때
                if (up) { // up이 true 인 상태, up을 취소시키는 일을 해야함.
                    // 1. 업의 상태 변경 -> 중복 코드
                    // 2. 숫자를 감소시킨다
                    // 3. 감소된 숫자를 텍스트 뷰에 설정한다.
                    // 4. 업의 이미지를 기본으로 바꿔준다.
                    up = !up
                    upCount -= 1
                    thumbupCount.text = upCount.toString()
                    thumbupImg.setImageResource(R.drawable.ic_thumb_up)
                } else { // up이 false 인 상태
                    // 1. 업의 상태 변경 -> 중복 코드
                    // 2. 숫자를 증가 시키고 텍스트 뷰에 설정한다.
                    // 3. 업의 이미지를 바꿔준다.
                    // 4. down 상태 체크
                    // 5. down 이 true면 down 상태 변경
                    // 6. down 숫자 감소
                    // 7. down 이미지 기본으로 변경
                    up = !up
                    upCount += 1
                    thumbupCount.text = upCount.toString()
                    thumbupImg.setImageResource(R.drawable.ic_thumb_up_selected)
                    if (down) { // down이 true 면 false로 바꿔야함.
                        down = !down
                        downCount -= 1
                        thumbdownCount.text = downCount.toString()
                        thumbdownImg.setImageResource(R.drawable.ic_thumb_down)
                    }
                }
            } else { // down 버튼에서 눌렀을 때, up 다운만 반대로 넣어주면 됨
                if (down) {
                    down = !down
                    downCount -= 1
                    thumbdownCount.text = downCount.toString()
                    thumbdownImg.setImageResource(R.drawable.ic_thumb_down)
                } else {
                    down = !down
                    downCount += 1
                    thumbdownCount.text = downCount.toString()
                    thumbdownImg.setImageResource(R.drawable.ic_thumb_down_selected)
                    if (up) { // down이 true 면 false로 바꿔야함.
                        up = !up
                        upCount -= 1
                        thumbupCount.text = upCount.toString()
                        thumbupImg.setImageResource(R.drawable.ic_thumb_up)
                    }
                }
            }
        }
    }


}