package best.hyun.cinema

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class ReviewItem: ConstraintLayout {
    private var id: TextView? = null
    constructor(context: Context) : super(context) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.list_item, this, true)
        id = findViewById<TextView>(R.id.text_userid)
    }

    fun setId(str: String) {
        id?.text = str
    }
}