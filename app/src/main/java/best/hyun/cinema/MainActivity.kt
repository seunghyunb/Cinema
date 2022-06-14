package best.hyun.cinema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity.LikeStatus"

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
            }
            else { // down 버튼에서 눌렀을 때, up 다운만 반대로 넣어주면 됨
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

    private val likeStatus = LikeStatus()
    private val thumbupImg: ImageView by lazy { findViewById(R.id.img_thumbup) }
    private val thumbdownImg: ImageView by lazy { findViewById(R.id.img_thumbdown) }
    private val thumbupCount: TextView by lazy { findViewById(R.id.text_thumbup) }
    private val thumbdownCount: TextView by lazy { findViewById(R.id.text_thumbdown) }
    private val showReviewListBtn: Button by lazy { findViewById(R.id.btn_show_reviewlist) }
    private val writeReviewBtn: Button by lazy { findViewById(R.id.btn_review_write) }
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thumbupImg.setOnClickListener {
            Log.d(TAG, "likeStatus.up: ${likeStatus.up}")
            likeStatus.changeStat(LIKESTAT.LIKE)
        }

        thumbdownImg.setOnClickListener{
            likeStatus.changeStat(LIKESTAT.DISLIKE)
        }

        listView = findViewById(R.id.listview_review)
        val adapter = ReviewAdapter()
        adapter.addItem(1)
        listView!!.adapter = adapter

        showReviewListBtn.setOnClickListener {
            val intent = Intent(applicationContext, ReviewListActivity::class.java)
            startActivity(intent)
        }

        writeReviewBtn.setOnClickListener {
            val intent = Intent(applicationContext, ReviewWriteActivity::class.java)
            startActivity(intent)
        }
    }

    inner class ReviewAdapter: BaseAdapter() {
        val items = ArrayList<Int>()

        fun addItem(item: Int) {
            items.add(item)
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
            val view = ReviewItem(applicationContext)
            val item = items[position]
            view.setId(item.toString())
            return view
        }
    }


}