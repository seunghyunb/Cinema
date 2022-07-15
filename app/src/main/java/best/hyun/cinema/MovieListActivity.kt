package best.hyun.cinema

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import best.hyun.cinema.dto.*
import best.hyun.cinema.retrofit.CommentListAPI
import best.hyun.cinema.retrofit.MovieInfoAPI
import best.hyun.cinema.retrofit.MovieListAPI
import com.google.android.material.navigation.NavigationView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/*
    영화 목록 화면을 담당한다.
*/

class MovieListActivity : AppCompatActivity(), FragmentToActivity,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "DpiTest"
    }

    private val drawer: DrawerLayout by lazy { findViewById(R.id.drawer) }
    private val navView: NavigationView by lazy { findViewById(R.id.nav_view) }
    private val pager: ViewPager2 by lazy { findViewById(R.id.pager_main) }
    private var pagerAdapter: PagerAdapter? = null
    private var isInfo: Boolean = false
    private var nowMovieTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 햄버거 메뉴로 바로가기 열기
        val toolbarMenu = findViewById<ImageView>(R.id.toolbar_menu)
        toolbarMenu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        // 네비게이션 뷰 아이템 선택 리스너 등록
        navView.setNavigationItemSelectedListener(this)

        pager.offscreenPageLimit = 1
        pagerAdapter = PagerAdapter.getInstance(this)
        pager.adapter = pagerAdapter

        val scale = resources.displayMetrics.density // 1dp당 1px 값, 현재 내 기기에서 1dp 당 1px 값은 2.625
//        val dpi = resources.displayMetrics.densityDpi // dpi 값, 현재 내 기기의 dpi는 420

        Log.d(
            TAG, "this.javaClass.name: ${this.javaClass.name}\n" +
                    "this.componentName: ${this.componentName}\n" +
                    "this.packageName: ${this.packageName}"
        )

        pager.setPageTransformer { page, position ->
            // page 는 현재 포커스되어 있는 뷰
            // position은 포커스되어 있는 뷰로 부터 떨어져 있는 비율 (-1.0f, 1.0f)
            page.translationX = -scale * 80.0f * position // 80dp 만큼 이동시켜 현재 화면에 일부가 보이도록 만든다.
        }

        // 영화 목록 요청하기
        requestMovieList()
    }

    override fun onResume() {
        super.onResume()
    }


    // 영화 목록 요청하기
    private fun requestMovieList() {
        if (NetworkManager.checkNetwork(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val movieListAPI: MovieListAPI = retrofit.create(MovieListAPI::class.java)
            val call = movieListAPI.readMovieList()
            call.enqueue(
                object : Callback<MovieListResult> {
                    override fun onResponse(
                        call: Call<MovieListResult>,
                        response: Response<MovieListResult>
                    ) {
                        if (!response.isSuccessful) {
                            Log.d(TAG, "${call.request().url()}: failed: ${response.code()}")
                            return
                        }

                        val movieListResult = response.body()

                        if (movieListResult != null) {
                            if (movieListResult.result.isNotEmpty()) {

                                // 영화 목록 로컬 저장하기 (1)
                                saveLocalMovieList(movieListResult.result)

                                for (movie in movieListResult.result) {
                                    pagerAdapter?.addItem(
                                        MovieFragment.newInstance(
                                            movie.id,
                                            movie.image,
                                            movie.title,
                                            "예매율 ${movie.reservation_rate}% | ${movie.grade}세 관람가"
                                        )
                                    )
                                }
                                pagerAdapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onFailure(call: Call<MovieListResult>, t: Throwable) {
                        Log.d(TAG, "${call.request().url()}: Fail: $t")
                    }
                }
            )
        } else {
            Thread {
                val db = AppDatabase.getInstance(applicationContext)
                val movieDao = db.movieDao()
                val movies = movieDao.getAllMovies()
                for (movie in movies) {
                    pagerAdapter?.addItem(
                        MovieFragment.newInstance(
                            movie.id,
                            movie.image,
                            movie.title,
                            "예매율 ${movie.reservation_rate}% | ${movie.grade}세 관람가"
                        )
                    )
                }
                pagerAdapter?.notifyDataSetChanged()
            }.start()
        }

    }

    //영화 목록 로컬 저장하기 (1)
    fun saveLocalMovieList(list: List<Movie>) {
        Thread {
            val db = AppDatabase.getInstance(applicationContext)
            val movieDao = db.movieDao()
            movieDao.insertMovieList(list)
        }.start()
    }

    // 영화 상세 정보 (3)
    // 영화 상세 정보 서버로 부터 받아오기
    private fun requestMovie(id: Int) {

        if (NetworkManager.checkNetwork(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build() // Retrofit 객체 생성
            val movieInfoAPI: MovieInfoAPI =
                retrofit.create(MovieInfoAPI::class.java) // 인터페이스 객체 생성
            val call: Call<MovieInfo> = movieInfoAPI.readMovie(id) // Call 객체 생성
            call.enqueue(
                object : Callback<MovieInfo> {
                    override fun onResponse(call: Call<MovieInfo>, response: Response<MovieInfo>) {
                        Log.d(
                            TAG,
                            "\nonResponse의 Call 객체\nCall<MovieInfo>\n$call\nResponse<MovieInfo>\n$response"
                        )
                        if (!response.isSuccessful)
                            return

                        val movieInfo = response.body() ?: return
                        val movie = movieInfo.result[0]

                        saveLocalMovie(movie)

                        val fm = supportFragmentManager
                        fm.beginTransaction()
                            .replace(
                                R.id.container,
                                MovieInfoFragment.newInstance(movie),
                                movie.title
                            )
                            .commit()

                        nowMovieTitle = movie.title
                        isInfo = true
                    }

                    override fun onFailure(call: Call<MovieInfo>, t: Throwable) {
                        Log.e(TAG, "requestMovie Fail >> $t")
                    }
                }
            )
        } else {
            Thread {
                val db = AppDatabase.getInstance(applicationContext)
                val movieDao = db.movieDao()
                val movie = movieDao.getMovie(id)

                val fm = supportFragmentManager
                fm.beginTransaction()
                    .replace(
                        R.id.container,
                        MovieInfoFragment.newInstance(movie),
                        movie.title
                    )
                    .commit()

                nowMovieTitle = movie.title
                isInfo = true
            }.start()
        }
    }

    fun saveLocalMovie(movie: Movie) {
        Thread {
            val db = AppDatabase.getInstance(applicationContext)
            val movieDao = db.movieDao()
            movieDao.insertMovie(movie)
        }.start()
    }

    // 영화 상세화면과 영화 목록 화면을 띄우는 일을 담당한다.
    override fun requestMovieInfo(command: String, id: Int?) {
        when (command) {
            "info" -> {
                // 영화 상세 정보 (2)
                // 영화 상세 정보 요청 메소드 실행
                if (id != null) {
                    requestMovie(id)
                }
            }

            "list" -> {
                val fm = supportFragmentManager

                val fragment = fm.findFragmentByTag(nowMovieTitle)
                fragment?.let {
                    fm.beginTransaction()
                        .remove(fragment)
                        .commit()
                }
            }
        }
    }

    // 한줄평 2개 가져오기 (2)
    override fun requestComment(id: Int, limit: Int, title: String) {
        if (NetworkManager.checkNetwork(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val commentListAPI = retrofit.create(CommentListAPI::class.java)
            val call = commentListAPI.readComment(id, limit)
            call.enqueue(
                object : Callback<CommentListResult> {
                    override fun onResponse(
                        call: Call<CommentListResult>,
                        response: Response<CommentListResult>
                    ) {
                        val commentListResult = response.body() ?: return
                        val commentList = commentListResult.result

                        saveLocalComments(commentList)

                        val fragment =
                            supportFragmentManager.findFragmentByTag(title) as? MovieInfoFragment
                        if (fragment != null) {
                            for (comment in commentList) {
                                // 한줄평 2개 가져오기 (3)
                                fragment.addReview(comment)
                            }
                        }
                    }

                    override fun onFailure(call: Call<CommentListResult>, t: Throwable) {}
                }
            )
        } else {
            Thread {
                val db = AppDatabase.getInstance(applicationContext)
                val commentDao = db.commentDao()
                val comments = commentDao.getComments(id)

                val fragment =
                    supportFragmentManager.findFragmentByTag(title) as? MovieInfoFragment
                if (fragment != null) {
                    for (comment in comments ) {
                        // 한줄평 2개 가져오기 (3)
                        fragment.addReview(comment)
                    }
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

    // 프래그먼트에서 다른 액티비티로 이동하는 일을 담당한다
    override fun moveActivity(name: String, bundle: Bundle?) {
        startActivity(Intent().apply {
            component = ComponentName(packageName, name)
            addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            if (bundle != null) {
                putExtras(bundle)
            }
        })
    }

    // 네비게이션 메뉴 선택시 동작함
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_movie_list -> {
                requestMovieInfo("list")
            }

            R.id.menu_movie_buy -> {
                startActivity(Intent(applicationContext, PictureActivity::class.java))
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // 영화 상세로 이동했을 때 영화 상세화면을 나갈 수 있도록 수정했음
    override fun onBackPressed() {
        if (isInfo) {
            requestMovieInfo("list")
            isInfo = false
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // 뷰 페이저의 어댑터 클래스
    private class PagerAdapter private constructor(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {

        companion object {
            private var INSTANCE: PagerAdapter? = null
            fun getInstance(fa: FragmentActivity) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: PagerAdapter(fa).also {
                        INSTANCE = it
                    }
                }
        }

        val items = ArrayList<Fragment>()
        override fun getItemCount(): Int {
            return items.size
        }

        override fun createFragment(position: Int): Fragment {
            return items[position]
        }

        fun addItem(item: Fragment) {
            items.add(item)
        }

        fun isEmpty(): Boolean {
            return INSTANCE == null
        }

        fun isNotEmpty(): Boolean {
            return !isEmpty()
        }
    }

}
