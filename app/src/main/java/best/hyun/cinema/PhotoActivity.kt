package best.hyun.cinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso

class PictureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val bundle = intent.extras
        val url = bundle?.getString("url")

        val photoView = findViewById<PhotoView>(R.id.img_photo)
        Picasso.get().load(url).into(photoView)
    }

}