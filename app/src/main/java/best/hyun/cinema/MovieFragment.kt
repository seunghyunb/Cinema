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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "poster"
private const val ARG_PARAM2 = "title"
private const val ARG_PARAM3 = "info"

/**
 * A simple [Fragment] subclass.
 * Use the [MovieFragment.newInstance]  factory method to
 * create an instance of this fragment.
 */
class MovieFragment : Fragment() {

    private var resId: Int? = null
    private var title: String? = null
    private var info: String? = null
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
            resId = it.getInt(ARG_PARAM1)
            title = it.getString(ARG_PARAM2)
            info = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        val imgPoster = view.findViewById<ImageView>(R.id.img_mv_poster)
        resId?.let { imgPoster.setImageResource(it) }
        val textTitle = view.findViewById<TextView>(R.id.text_mv_title)
        title?.let { textTitle.text = it }
        val textInfo = view.findViewById<TextView>(R.id.text_mv_info)
        info?.let { textInfo.text = it }

        // 영화 상세 프래그먼트로 바꾸기
        val btnDetail = view.findViewById<Button>(R.id.btn_mv_detail)
        btnDetail.setOnClickListener {
            fta?.let {
                it.changeFragment("info")
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String, param3: String) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }
}