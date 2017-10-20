package pl.applover.intervalrangebar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout


/**
 * Created by janpawlov ( ͡° ͜ʖ ͡°) on 11/10/2017.
 */
class IntervalRangeBar : FrameLayout {

    private val DEFAULT_MIN_VALUE = 0
    private val DEFAULT_MAX_VALUE = 24
    private val DEFAULT_LEFT_TICK_VALUE = 0
    private val DEFAULT_RIGHT_TICK_VALUE = 24
    private val DEFAULT_INTERVAL = 1
    private val DEFAULT_THUMB_DRAWABLE = R.drawable.default_thumb
    private val DEFAULT_BACKGROUND_DRAWABLE = R.drawable.background_track
    private val DEFAULT_BAR_HEIGHT = context.resources.getDimensionPixelSize(R.dimen.def_bar_height)
    private val DEFAULT_BAR_WIDTH = FrameLayout.LayoutParams.MATCH_PARENT
    private val DEFAULT_THUMB_SIZE = context.resources.getDimensionPixelSize(R.dimen.def_thumb_size)

    private var minValue = DEFAULT_MIN_VALUE
    private var maxValue = DEFAULT_MAX_VALUE
    private var leftThumbValue = DEFAULT_LEFT_TICK_VALUE
    private var rightThumbValue = DEFAULT_RIGHT_TICK_VALUE
    private var leftThumbDrawable = DEFAULT_THUMB_DRAWABLE
    private var rightThumbDrawable = DEFAULT_THUMB_DRAWABLE
    private var thumbSize = DEFAULT_THUMB_SIZE
    private var backgroundDrawable = DEFAULT_BACKGROUND_DRAWABLE
    private var barWidth = DEFAULT_BAR_WIDTH
    private var barHeight = DEFAULT_BAR_HEIGHT
    private var interval = DEFAULT_INTERVAL

    private var background: BackgroundBar? = null
    private var leftThumb: Thumb? = null
    private var rightThumb: Thumb? = null
    private var connectingLine: ConnectingLine? = null

    private var dX: Float = 0f
    private var intervals: Int = 0
    /** Flag determining relative position of thumbs */
    private var isLeftThumbOnLeft: Boolean = true

    private var mListener: ThumbsValueListener? = null

    constructor(context: Context?) : super(context) {
        initBar(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initBar(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initBar(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**Position buttons every time view is measured**/
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /** Buttons must be positioned with measured width, otherwise positioning might be incorrect*/
        leftThumb?.positionButton(widthSize)
        rightThumb?.positionButton(widthSize)
        connectingLine?.invalidate()

    }

    fun attachValueListener(listener: ThumbsValueListener) {
        this.mListener = listener
    }

    fun setStartValues(leftThumbValue: Int, rightThumbValue: Int) {
        leftThumb?.currentValue = leftThumbValue
        rightThumb?.currentValue = rightThumbValue
        checkThumbValues()
        connectingLine!!.invalidate()
        leftThumb!!.positionButton(background!!.width)
        rightThumb!!.positionButton(background!!.width)
        /**Set values for thumbs, measure the view to properly position the buttons afterwards*//*
        measure(View.MeasureSpec.makeMeasureSpec(background!!.width, MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(thumbSize, MeasureSpec.EXACTLY))*/
    }

    private fun initBar(context: Context?, attrs: AttributeSet? = null) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.IntervalRangeBar, 0, 0)
        if (typedArray != null) {
            val valueStart = typedArray.getInt(R.styleable.IntervalRangeBar_minValue, DEFAULT_MIN_VALUE)
            val valueEnd = typedArray.getInt(R.styleable.IntervalRangeBar_maxValue, DEFAULT_MAX_VALUE)
            val startLeftThumb = typedArray.getInt(R.styleable.IntervalRangeBar_leftStartValue, DEFAULT_LEFT_TICK_VALUE)
            val endRightThumb = typedArray.getInt(R.styleable.IntervalRangeBar_rightStartValue, DEFAULT_RIGHT_TICK_VALUE)
            if (areStartValuesValid(valueStart, valueEnd, startLeftThumb, endRightThumb)) {
                minValue = valueStart
                maxValue = valueEnd
                leftThumbValue = startLeftThumb
                rightThumbValue = endRightThumb
                isLeftThumbOnLeft = leftThumbValue <= rightThumbValue
            }
            leftThumbDrawable = typedArray.getInt(R.styleable.IntervalRangeBar_leftThumb, DEFAULT_THUMB_DRAWABLE)
            rightThumbDrawable = typedArray.getInt(R.styleable.IntervalRangeBar_rightThumb, DEFAULT_THUMB_DRAWABLE)
            thumbSize = typedArray.getDimensionPixelSize(R.styleable.IntervalRangeBar_thumbSize, DEFAULT_THUMB_SIZE)
            backgroundDrawable = typedArray.getInt(R.styleable.IntervalRangeBar_barBackground, DEFAULT_BACKGROUND_DRAWABLE)
            interval = typedArray.getInt(R.styleable.IntervalRangeBar_interval, DEFAULT_INTERVAL)
            barHeight = typedArray.getDimensionPixelSize(R.styleable.IntervalRangeBar_barHeight, DEFAULT_BAR_HEIGHT)

            if (barHeight > thumbSize)
                barHeight = thumbSize

            val width = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")
            if (width == "match_parent")
                barWidth = DEFAULT_BAR_WIDTH

        }
        typedArray?.recycle()
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, thumbSize, Gravity.CENTER_VERTICAL)
        initContent()
    }

    private fun initContent() {
        intervals = maxValue - minValue + 1

        background = BackgroundBar(context, barHeight, backgroundDrawable)
        addView(background)

        //left thumb is on the right, change background color
        if (!isLeftThumbOnLeft)
            (background!!.background as GradientDrawable).setColor(context.resources.getColor(R.color.default_selected))

        leftThumb = Thumb(context, thumbSize, leftThumbDrawable, leftThumbValue, intervals)
        addView(leftThumb)

        rightThumb = Thumb(context, thumbSize, rightThumbDrawable, rightThumbValue, intervals)
        addView(rightThumb)

        connectingLine = ConnectingLine(context, thumbSize, leftThumb!!, rightThumb!!, background!!)
        addView(connectingLine)

        leftThumb!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> dX = view.x - motionEvent.rawX
                MotionEvent.ACTION_MOVE -> {
                    if (view is Thumb) {
                        view.animate()
                                .x(if (view.x + (view.width / 2) <= background!!.width) motionEvent.rawX + dX else (background!!.width - view.width).toFloat())
                                .setDuration(0)
                                .start()
                        if (view.x <= (view.width / 8))
                        /**X position of the view is close to the 0, block further movement*/
                            view.animate()
                                    .x(0f)
                                    .setDuration(0)
                                    .start()
                        view.updateValue(background!!.width, { mListener?.thumbsValuesUpdated(leftThumb!!.currentValue, rightThumb!!.currentValue) })
                        connectingLine!!.invalidate()
                        checkThumbValues()
                    }
                }
                MotionEvent.ACTION_UP -> (view as Thumb).positionButton(background!!.width, { connectingLine!!.invalidate() })
                else -> return@OnTouchListener false
            }
            true
        })

        rightThumb!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> dX = view.x - motionEvent.rawX
                MotionEvent.ACTION_MOVE -> {
                    if (view is Thumb) {
                        view.animate()
                                .x(if (view.x + (view.width / 2) <= background!!.width) motionEvent.rawX + dX else (background!!.width - view.width).toFloat())
                                .setDuration(0)
                                .start()
                        if (view.x <= (view.width / 8))
                        /**X position of the view is close to the 0, block further movement*/
                            view.animate()
                                    .x(0f)
                                    .setDuration(0)
                                    .start()
                        view.updateValue(background!!.width, { mListener?.thumbsValuesUpdated(leftThumb!!.currentValue, rightThumb!!.currentValue) })
                        connectingLine!!.invalidate()
                        checkThumbValues()
                    }
                    mListener?.thumbsValuesUpdated(leftThumb!!.currentValue, rightThumb!!.currentValue)
                }
                MotionEvent.ACTION_UP -> (view as Thumb).positionButton(background!!.width, { connectingLine!!.invalidate() })
                else -> return@OnTouchListener false
            }
            true
        })
    }

    /**Call every time you set/change pin values!*/
    private fun checkThumbValues() {
        if (isLeftThumbOnLeft && leftThumb!!.currentValue > rightThumb!!.currentValue) { //left thumb was on the left to this point
            isLeftThumbOnLeft = false
            (background!!.background as GradientDrawable).setColor(context.resources.getColor(R.color.default_selected))
        }
        if (!isLeftThumbOnLeft && leftThumb!!.currentValue < rightThumb!!.currentValue) { //left thumb was on the right to this point
            isLeftThumbOnLeft = true
            (background!!.background as GradientDrawable).setColor(context.resources.getColor(R.color.default_background))
        }
    }

    private fun areStartValuesValid(valueStart: Int, valueEnd: Int, startLeftThumb: Int, endRightThumb: Int): Boolean {
        val isRangePositive = valueStart >= 0 && valueEnd >= 0
        val isStartSmaller = valueStart < valueEnd
        val isLongEnough = valueStart + 2 < valueEnd
        val isLeftInRange = startLeftThumb in valueStart..valueEnd
        val isRightInRange = endRightThumb in valueStart..valueEnd
        val areThumbsDifferent = startLeftThumb != endRightThumb
        return isRangePositive && isStartSmaller && isLongEnough && isLeftInRange && isRightInRange && areThumbsDifferent
    }

    interface ThumbsValueListener {
        fun thumbsValuesUpdated(leftThumbValue: Int, rightThumbValue: Int)
    }
}