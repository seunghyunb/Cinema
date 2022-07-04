package best.hyun.cinema

import android.util.Log
import best.hyun.cinema.dto.SimpleStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import best.hyun.cinema.retrofit.CommentWriteAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewWriteActivity : AppCompatActivity() {
    private val TAG = "WriteAPI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_write)

        val title: TextView = findViewById(R.id.text_title)
        val grade: ImageView = findViewById(R.id.img_grade)
        val save: Button = findViewById(R.id.btn_save)
        val edit: EditText = findViewById(R.id.edit_review)
        val rtbar: RatingBar = findViewById(R.id.rtbar_write)

        edit.setText("재밌다")

        val bundle = intent.extras
        if (bundle != null) {
            title.text = bundle.getString("title")
            setGradeImage(bundle.getInt("grade"), grade)

            save.setOnClickListener {
                val retrofit = Retrofit.Builder()
                    .baseUrl(resources.getString(R.string.baseUrl))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val commentWriteAPI = retrofit.create(CommentWriteAPI::class.java)

                val call = commentWriteAPI.writeComment(bundle.getInt("id"), "hyun100", rtbar.rating, edit.text.toString())
                call.enqueue(
                    object : Callback<SimpleStatus> {
                        override fun onResponse(
                            call: Call<SimpleStatus>,
                            response: Response<SimpleStatus>
                        ) {
                            if (response.isSuccessful)
                                finish()
                        }

                        override fun onFailure(call: Call<SimpleStatus>, t: Throwable) {
                            Log.d(TAG, "실패\n$t")
                        }
                    }
                )

            }
        }

        val cancel: Button = findViewById(R.id.btn_cancel)
        cancel.setOnClickListener {
            finish()
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
}