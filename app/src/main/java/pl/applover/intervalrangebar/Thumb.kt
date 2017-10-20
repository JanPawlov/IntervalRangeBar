package pl.applover.intervalrangebar

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

/**
 * Created by janpawlov ( ͡° ͜ʖ ͡°) on 11/10/2017.
 */
class Thumb(context: Context,private val thumbSize: Int, thumbDrawable: Int, var currentValue: Int, private val intervals: Int) : View(context) {

    init {
        this.layoutParams = FrameLayout.LayoutParams(thumbSize, thumbSize, Gravity.CENTER_VERTICAL)
        this.background = context.getDrawable(thumbDrawable)
        this.elevation = 4f
    }

    fun updateValue(barWidth: Int, completion: (() -> Unit)? = null) {
        val widthPerInterval = (barWidth) / intervals
        val updatedValue = (x / widthPerInterval)
        val rounded = Math.round(updatedValue)
        if (rounded != currentValue) {
            currentValue = if (rounded > intervals - 1)
                intervals - 1
            else
                rounded
            completion?.invoke()
        }
    }

    /**Must override this to supress warning*/
    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun positionButton(barWidth: Int, completion: (() -> Unit)? = null) {
        /**barWidth must be given explicitly from onMeasure result */
        val widthPerInterval = (barWidth.toFloat()) / intervals
        x = if (currentValue == intervals - 1)
            (barWidth.toFloat() - thumbSize)
        else
            (currentValue) * widthPerInterval

        completion?.invoke()
    }
}