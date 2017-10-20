package pl.applover.intervalrangebar

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

/**
 * Created by janpawlov ( ͡° ͜ʖ ͡°) on 11/10/2017.
 */
class BackgroundBar(context: Context, val barHeight: Int, backgroundDrawable: Int) : View(context) {
    init {
        this.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, barHeight, Gravity.CENTER)
        this.background = context.getDrawable(backgroundDrawable)
        this.elevation = 0f
    }
}