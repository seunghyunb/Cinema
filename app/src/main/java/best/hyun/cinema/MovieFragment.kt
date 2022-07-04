package best.hyun.cinema

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "poster"
private const val ARG_PARAM3 = "title"
private const val ARG_PARAM4 = "info"

/**
 * A simple [Fragment] subclass.
 * Use the [MovieFragment.newInstance]  factory method to
 * create an instance of this fragment.
 */
class MovieFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String, param3: String, param4: String) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putString(ARG_PARAM4, param4)
                }
            }
    }

    private var id: Int? = null // 영화 id(1)
    private var imgSrc: String? = null // 이미지(image)
    private var title: String? = null // 꾼(title)
    private var info: String? = null // 예매율 61.69(reservation_rate)% | 15(grade)세 관람가
    private var fta: FragmentToActivity? = null

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
        arguments?.let {
            id = it.getInt(ARG_PARAM1)
            imgSrc = it.getString(ARG_PARAM2)
            title = it.getString(ARG_PARAM3)
            info = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        val imgPoster = view.findViewById<ImageView>(R.id.img_mv_poster)
        imgSrc?.let { Picasso.get().load(imgSrc).into(imgPoster) }
        val textTitle = view.findViewById<TextView>(R.id.text_mv_title)
        title?.let { textTitle.text = it }
        val textInfo = view.findViewById<TextView>(R.id.text_mv_info)
        info?.let { textInfo.text = it }

        // 영화 상세 프래그먼트로 바꾸기
        val btnDetail = view.findViewById<Button>(R.id.btn_mv_detail)
        btnDetail.setOnClickListener {
            fta?.let {
                // 영화 상세 정보 (1)
                it.requestMovieInfo("info", id)
            }
        }

        return view
    }


}