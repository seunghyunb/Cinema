package best.hyun.cinema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import best.hyun.cinema.dto.Comment
import best.hyun.cinema.dto.CommentListResult
import best.hyun.cinema.retrofit.CommentListAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewListActivity : AppCompatActivity() {

    private var adapter: CommentAdapter? = null
    private val audience: TextView by lazy { findViewById(R.id.text_audience) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val title = findViewById<TextView>(R.id.text_title)
        val grade = findViewById<ImageView>(R.id.img_grade)
        val audienceRating = findViewById<TextView>(R.id.text_audience_rating)
        val listview = findViewById<ListView>(R.id.listview_review)
        adapter = CommentAdapter()
        listview.adapter = adapter
        val writeReview = findViewById<Button>(R.id.btn_review_write)
        val rtbar = findViewById<RatingBar>(R.id.rtbar_review_list)

        val bundle = intent.extras
        if (bundle != null) {
            title.text = bundle.getString("title")
            setGradeImage(bundle.getInt("grade"), grade)
            audienceRating.text = "${bundle.getFloat("audienceRating")}"
            rtbar.rating = (bundle.getFloat("audienceRating") / 2)

            // 한줄평 모두 보기 (3)
            requestComment(bundle.getInt("id"))

            writeReview.setOnClickListener {
                startActivity(Intent(this, ReviewWriteActivity::class.java).apply {
                    putExtra("id", bundle.getInt("id"))
                    putExtra("title", bundle.getString("title"))
                    putExtra("grade", bundle.getInt("grade"))
                })
            }
        }

        val imgBack = findViewById<ImageView>(R.id.img_back)
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setGradeImage(grade: Int, img: ImageView) {
        when (grade) {
            12 -> img.setImageResource(R.drawable.ic_12)
            15 -> img.setImageResource(R.drawable.ic_15)
            19 -> img.setImageResource(R.drawable.ic_19)
            else -> img.setImageResource(R.drawable.ic_all)
        }
    }

    // 한줄평 모두 보기 (3)
    private fun requestComment(id: Int) {
        if (NetworkManager.checkNetwork(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val commentListAPI = retrofit.create(CommentListAPI::class.java)
            val call = commentListAPI.readComment(id, 100)
            call.enqueue(
                object : Callback<CommentListResult> {
                    override fun onResponse(
                        call: Call<CommentListResult>,
                        response: Response<CommentListResult>
                    ) {
                        val commentListResult = response.body() ?: return
                        val comments = commentListResult.result

                        saveLocalComments(comments)

                        val totalCount = commentListResult.totalCount
                        audience.text = "(${DecimalFormat("#,###").format(totalCount)}명 참여)"

                        if (adapter == null)
                            return

                        for (comment in comments)
                            adapter!!.addItem(comment)
                    }

                    override fun onFailure(call: Call<CommentListResult>, t: Throwable) {}
                }
            )
        } else {
            Thread {
                val db = AppDatabase.getInstance(applicationContext)
                val commentDao = db.commentDao()
                val comments = commentDao.getAllComments(id)
                val totalCount = comments.size
                if (adapter != null) {
                    for (comment in comments)
                        adapter!!.addItem(comment)
                }
                Handler(Looper.getMainLooper()).post {
                    audience.text = "${DecimalFormat("#,###").format(totalCount)}"
                }
            }.start()
        }
    }

    private fun saveLocalComments(comments: List<Comment>) {
        Thread {
            val db = AppDatabase.getInstance(applicationContext)
            val commentDao = db.commentDao()
            commentDao.insertCommentList(comments)
        }.start()
    }


    inner class CommentAdapter : BaseAdapter() {
        private val items = ArrayList<Comment>()

        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(p0: Int): Any {
            return items[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val view = ReviewItem(this@ReviewListActivity)

            val comment = items[p0]

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

        fun addItem(comment: Comment) {
            items.add(comment)
            notifyDataSetChanged()
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


}