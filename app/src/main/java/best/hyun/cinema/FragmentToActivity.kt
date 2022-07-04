package best.hyun.cinema

import android.app.Activity
import android.os.Bundle

interface FragmentToActivity {
    fun moveActivity(name: String, bundle: Bundle? = null)
    fun requestMovieInfo(command: String, id: Int? = null)
    fun requestComment(id: Int, limit: Int, title: String)
}