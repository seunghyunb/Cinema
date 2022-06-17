package best.hyun.cinema

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView

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
    private var isInfo: Boolean = false

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
        val pagerAdapter = PagerAdapter(this)
        pagerAdapter.addItem(
            MovieFragment.newInstance(
                R.drawable.image1,
                "1.군도",
                "예매율 61.6% | 15세 관람가 | D-1"
            )
        )
        pagerAdapter.addItem(
            MovieFragment.newInstance(
                R.drawable.image2,
                "2.공조",
                "예매율 61.6% | 15세 관람가 | D-1"
            )
        )

        pagerAdapter.addItem(
            MovieFragment.newInstance(
                R.drawable.image3,
                "3.더킹",
                "예매율 11.2% | 12세 관람가"
            )
        )
        pagerAdapter.addItem(
            MovieFragment.newInstance(
                R.drawable.image4,
                "4.레지던트 이블",
                "예매율 21.6% | 15세 관람가 | D-2"
            )
        )
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


    }

    // 뷰 페이저의 어댑터 클래스
    private class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
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
    }

    // 프래그먼트에서 다른 액티비티로 이동하는 일을 담당한다
    override fun moveActivity(name: String) {
        startActivity(Intent().apply {
            component = ComponentName(packageName, name)
            addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        })
    }

    // 영화 상세화면과 영화 목록 화면을 띄우는 일을 담당한다.
    override fun changeFragment(command: String) {
        when (command) {
            "info" -> {
                val fm = supportFragmentManager

                fm.beginTransaction()
                    .replace(R.id.container, MovieInfoActivity(), "info")
                    .commit()

                isInfo = true
            }

            "list" -> {
                val fm = supportFragmentManager

                val fragment = fm.findFragmentByTag("info")
                fragment?.let {
                    fm.beginTransaction()
                        .remove(fragment)
                        .commit()
                }
            }
        }

    }

    // 네비게이션 메뉴 선택시 동작함
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_movie_list -> {
                changeFragment("list")
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // 영화 상세로 이동했을 때 영화 상세화면을 나갈 수 있도록 수정했음
    override fun onBackPressed() {
        if (isInfo) {
            changeFragment("list")
            isInfo = false
        } else {
            super.onBackPressed()
        }
    }
}
