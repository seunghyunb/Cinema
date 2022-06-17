package best.hyun.cinema

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class MovieInfoActivity : Fragment() {

    private val TAG = "MainActivity.LikeStatus"

    private var fta: FragmentToActivity? = null
    private val likeStatus = LikeStatus()
    private lateinit var thumbupImg: ImageView
    private lateinit var thumbdownImg: ImageView
    private lateinit var thumbupCount: TextView
    private lateinit var thumbdownCount: TextView
    private lateinit var showReviewListBtn: Button
    private lateinit var writeReviewBtn: Button
    private lateinit var listView: ListView

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_movie_info, container, false)

        thumbupImg = view.findViewById(R.id.img_thumbup)
        thumbdownImg = view.findViewById(R.id.img_thumbdown)
        thumbupCount = view.findViewById(R.id.text_thumbup)
        thumbdownCount = view.findViewById(R.id.text_thumbdown)
        showReviewListBtn = view.findViewById(R.id.btn_show_reviewlist)
        writeReviewBtn = view.findViewById(R.id.btn_review_write)

        thumbupImg.setOnClickListener {
            Log.d(TAG, "likeStatus.up: ${likeStatus.up}")
            likeStatus.changeStat(LIKESTAT.LIKE)
        }

        thumbdownImg.setOnClickListener {
            likeStatus.changeStat(LIKESTAT.DISLIKE)
        }

        listView = view.findViewById(R.id.listview_review)
        val adapter = ReviewAdapter()
        adapter.addItem(1)
        listView!!.adapter = adapter

        showReviewListBtn.setOnClickListener {
            fta?.let {
                it.moveActivity("best.hyun.cinema.ReviewListActivity")
            }
        }

        writeReviewBtn.setOnClickListener {
            fta?.let {
                it.moveActivity("best.hyun.cinema.ReviewWriteActivity")
            }
        }

        return return view
    }

    inner class ReviewAdapter : BaseAdapter() {
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
            val view = ReviewItem(requireContext())
            val item = items[position]
            view.setId(item.toString())
            return view
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