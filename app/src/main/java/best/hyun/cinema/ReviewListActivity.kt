package best.hyun.cinema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ReviewListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val imgBack = findViewById<ImageView>(R.id.img_back)
        imgBack.setOnClickListener{
            onBackPressed()
        }
    }
}