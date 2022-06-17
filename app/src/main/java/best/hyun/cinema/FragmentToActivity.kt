package best.hyun.cinema

import android.app.Activity

interface FragmentToActivity {
    fun moveActivity(name: String)
    fun changeFragment(command: String)
}