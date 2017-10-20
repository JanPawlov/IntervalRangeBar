package pl.applover.intervalrangebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

/**
 * Created by janpawlov ( ͡° ͜ʖ ͡°) on 13/10/2017.
 */
class ConnectingLine(context: Context, thumbSize: Int, private val leftThumb: Thumb?, private val rightThumb: Thumb?, backgroundBar: BackgroundBar?) : View(context) {
    init {
        this.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, thumbSize, Gravity.CENTER)
        this.elevation = 2f
        setWillNotDraw(false)
    }


    private fun isLeftThumbOnLeft(): Boolean {
        return leftThumb!!.currentValue <= rightThumb!!.currentValue
    }

    /**Top and bottom height values for rectangle**/
    private val rectTop = (thumbSize - backgroundBar!!.barHeight) / 2
    private val rectBottom = (thumbSize - backgroundBar!!.barHeight) / 2 + backgroundBar.barHeight


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = if (isLeftThumbOnLeft()) context.resources.getColor(R.color.default_selected) else context.resources.getColor(R.color.default_background)
        paint.isAntiAlias = true

        /**Build rectangle to draw in relation to thumbs position & bar height**/
        if (isLeftThumbOnLeft())
            rect.set(leftThumb!!.x.toInt() + (leftThumb.width / 2), rectTop, rightThumb!!.x.toInt() + (rightThumb.width / 2), rectBottom)
        else
            rect.set(rightThumb!!.x.toInt() + (rightThumb.width / 2), rectTop, leftThumb!!.x.toInt() + (leftThumb.width / 2), rectBottom)

        canvas?.drawRect(rect, paint)
    }

    companion object {
        val paint = Paint()
        val rect = Rect()
    }

}