package com.example.unacademyassignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*


class CircularProgressBar : View {

    private val DPTOPX_SCALE = resources.displayMetrics.density

    private val MIN_TOUCH_TARGET_DP = 48f

    private var mCirclePaint: Paint? = null

    private var mCircleFillPaint: Paint? = null

    private var mCircleProgressPaint: Paint? = null

    private var mCircleProgressGlowPaint: Paint? = null

    private var mPointerPaint: Paint? = null

    private var mPointerHaloPaint: Paint? = null

    private var mPointerHaloBorderPaint: Paint? = null

    private var mCircleStrokeWidth = 0f

    private var mCircleXRadius = 0f

    private var mCircleYRadius = 0f

    private var mPointerRadius = 0f

    private var mPointerHaloWidth = 0f

    private var mPointerHaloBorderWidth = 0f

    private var mStartAngle = 0f

    private var mEndAngle = 0f

    private var mCircleRectF = RectF()

    private var mPointerColor = DEFAULT_POINTER_COLOR

    private var mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR

    private var mPointerHaloColorOnTouch =
        DEFAULT_POINTER_HALO_COLOR_ONTOUCH

    private var mCircleColor = DEFAULT_CIRCLE_COLOR

    private var mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR

    private var mCircleProgressColor =
        DEFAULT_CIRCLE_PROGRESS_COLOR

    private var mPointerAlpha = DEFAULT_POINTER_ALPHA

    private var mPointerAlphaOnTouch =
        DEFAULT_POINTER_ALPHA_ONTOUCH

    private var mTotalCircleDegrees = 0f

    private var mProgressDegrees = 0f

    private var mCirclePath: Path? = null

    private var mCircleProgressPath: Path? = null

    private var mMax = 0

    private var mProgress = 0

    private var mCustomRadii = false

    private var mMaintainEqualCircle = false

    private var mMoveOutsideCircle = false


    private var isLockEnabled = true

    private var lockAtStart = true

    private var lockAtEnd = false

    private var mUserIsMovingPointer = false

    private var cwDistanceFromStart = 0f

    private var ccwDistanceFromStart = 0f

    private var cwDistanceFromEnd = 0f

    private var ccwDistanceFromEnd = 0f

    private var lastCWDistanceFromStart = 0f

    private var cwDistanceFromPointer = 0f

    private var ccwDistanceFromPointer = 0f

    private var mIsMovingCW = false

    private var mCircleWidth = 0f

    private var mCircleHeight = 0f

    private var mPointerPosition = 0f

    private var mPointerPositionXY = FloatArray(2)

    private var mOnCircularProgressBarChangeListener: OnCircularProgressBarChangeListener? = null

    private var isTouchEnabled = false

    private fun initAttributes(attrArray: TypedArray) {
        mCircleXRadius = attrArray.getDimension(
            R.styleable.CircularProgressBar_circle_x_radius,
            DEFAULT_CIRCLE_X_RADIUS * DPTOPX_SCALE
        )
        mCircleYRadius = attrArray.getDimension(
            R.styleable.CircularProgressBar_circle_y_radius,
            DEFAULT_CIRCLE_Y_RADIUS * DPTOPX_SCALE
        )
        mPointerRadius = attrArray.getDimension(
            R.styleable.CircularProgressBar_pointer_radius,
            DEFAULT_POINTER_RADIUS * DPTOPX_SCALE
        )
        mPointerHaloWidth = attrArray.getDimension(
            R.styleable.CircularProgressBar_pointer_halo_width,
            DEFAULT_POINTER_HALO_WIDTH * DPTOPX_SCALE
        )
        mPointerHaloBorderWidth = attrArray.getDimension(
            R.styleable.CircularProgressBar_pointer_halo_border_width,
            DEFAULT_POINTER_HALO_BORDER_WIDTH * DPTOPX_SCALE
        )
        mCircleStrokeWidth = attrArray.getDimension(
            R.styleable.CircularProgressBar_circle_stroke_width,
            DEFAULT_CIRCLE_STROKE_WIDTH * DPTOPX_SCALE
        )
        mPointerColor = attrArray.getColor(
            R.styleable.CircularProgressBar_pointer_color,
            DEFAULT_POINTER_COLOR
        )
        mPointerHaloColor = attrArray.getColor(
            R.styleable.CircularProgressBar_pointer_halo_color,
            DEFAULT_POINTER_HALO_COLOR
        )
        mPointerHaloColorOnTouch = attrArray.getColor(
            R.styleable.CircularProgressBar_pointer_halo_color_ontouch,
            DEFAULT_POINTER_HALO_COLOR_ONTOUCH
        )
        mCircleColor = attrArray.getColor(
            R.styleable.CircularProgressBar_circle_color,
            DEFAULT_CIRCLE_COLOR
        )
        mCircleProgressColor = attrArray.getColor(
            R.styleable.CircularProgressBar_circle_progress_color,
            DEFAULT_CIRCLE_PROGRESS_COLOR
        )
        mCircleFillColor = attrArray.getColor(
            R.styleable.CircularProgressBar_circle_fill,
            DEFAULT_CIRCLE_FILL_COLOR
        )
        mPointerAlpha = Color.alpha(mPointerHaloColor)
        mPointerAlphaOnTouch = attrArray.getInt(
            R.styleable.CircularProgressBar_pointer_alpha_ontouch,
            DEFAULT_POINTER_ALPHA_ONTOUCH
        )
        if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0) {
            mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH
        }
        mMax =
            attrArray.getInt(R.styleable.CircularProgressBar_max, DEFAULT_MAX)
        mProgress = attrArray.getInt(
            R.styleable.CircularProgressBar_progress,
            DEFAULT_PROGRESS
        )
        mCustomRadii = attrArray.getBoolean(
            R.styleable.CircularProgressBar_use_custom_radii,
            DEFAULT_USE_CUSTOM_RADII
        )
        mMaintainEqualCircle = attrArray.getBoolean(
            R.styleable.CircularProgressBar_maintain_equal_circle,
            DEFAULT_MAINTAIN_EQUAL_CIRCLE
        )
        mMoveOutsideCircle = attrArray.getBoolean(
            R.styleable.CircularProgressBar_move_outside_circle,
            DEFAULT_MOVE_OUTSIDE_CIRCLE
        )
        isLockEnabled = attrArray.getBoolean(
            R.styleable.CircularProgressBar_lock_enabled,
            DEFAULT_LOCK_ENABLED
        )
        mStartAngle = (360f + attrArray.getFloat(
            R.styleable.CircularProgressBar_start_angle,
            DEFAULT_START_ANGLE
        ) % 360f) % 360f
        mEndAngle = (360f + attrArray.getFloat(
            R.styleable.CircularProgressBar_end_angle,
            DEFAULT_END_ANGLE
        ) % 360f) % 360f
        if (mStartAngle == mEndAngle) {
            mEndAngle -= .1f
        }
    }


    private fun initPaints() {
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mCirclePaint!!.isDither = true
        mCirclePaint!!.color = mCircleColor
        mCirclePaint!!.strokeWidth = mCircleStrokeWidth
        mCirclePaint!!.style = Paint.Style.STROKE
        mCirclePaint!!.strokeJoin = Paint.Join.ROUND
        mCirclePaint!!.strokeCap = Paint.Cap.ROUND
        mCircleFillPaint = Paint()
        mCircleFillPaint!!.isAntiAlias = true
        mCircleFillPaint!!.isDither = true
        mCircleFillPaint!!.color = mCircleFillColor
        mCircleFillPaint!!.style = Paint.Style.FILL
        mCircleProgressPaint = Paint()
        mCircleProgressPaint!!.isAntiAlias = true
        mCircleProgressPaint!!.isDither = true
        mCircleProgressPaint!!.color = mCircleProgressColor
        mCircleProgressPaint!!.strokeWidth = mCircleStrokeWidth
        mCircleProgressPaint!!.style = Paint.Style.STROKE
        mCircleProgressPaint!!.strokeJoin = Paint.Join.ROUND
        mCircleProgressPaint!!.strokeCap = Paint.Cap.ROUND
        mCircleProgressGlowPaint = Paint()
        mCircleProgressGlowPaint!!.set(mCircleProgressPaint)
        mCircleProgressGlowPaint!!.maskFilter = BlurMaskFilter(
            5f * DPTOPX_SCALE,
            BlurMaskFilter.Blur.NORMAL
        )
        mPointerPaint = Paint()
        mPointerPaint!!.isAntiAlias = true
        mPointerPaint!!.isDither = true
        mPointerPaint!!.style = Paint.Style.FILL
        mPointerPaint!!.color = mPointerColor
        mPointerPaint!!.strokeWidth = mPointerRadius
        mPointerHaloPaint = Paint()
        mPointerHaloPaint!!.set(mPointerPaint)
        mPointerHaloPaint!!.color = mPointerHaloColor
        mPointerHaloPaint!!.alpha = mPointerAlpha
        mPointerHaloPaint!!.strokeWidth = mPointerRadius + mPointerHaloWidth
        mPointerHaloBorderPaint = Paint()
        mPointerHaloBorderPaint!!.set(mPointerPaint)
        mPointerHaloBorderPaint!!.strokeWidth = mPointerHaloBorderWidth
        mPointerHaloBorderPaint!!.style = Paint.Style.STROKE
    }


    private fun calculateTotalDegrees() {
        mTotalCircleDegrees =
            (360f - (mStartAngle - mEndAngle)) % 360f
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f
        }
    }


    private fun calculateProgressDegrees() {
        mProgressDegrees = mPointerPosition - mStartAngle
        mProgressDegrees =
            if (mProgressDegrees < 0) 360f + mProgressDegrees else mProgressDegrees
    }


    private fun calculatePointerAngle() {
        val progressPercent = mProgress.toFloat() / mMax.toFloat()
        mPointerPosition = progressPercent * mTotalCircleDegrees + mStartAngle
        mPointerPosition %= 360f
    }

    private fun calculatePointerXYPosition() {
        var pm = PathMeasure(mCircleProgressPath, false)
        var returnValue = pm.getPosTan(pm.length, mPointerPositionXY, null)
        if (!returnValue) {
            pm = PathMeasure(mCirclePath, false)
            returnValue = pm.getPosTan(0f, mPointerPositionXY, null)
        }
    }


    private fun initPaths() {
        mCirclePath = Path()
        mCirclePath!!.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees)
        mCircleProgressPath = Path()
        mCircleProgressPath!!.addArc(mCircleRectF, mStartAngle, mProgressDegrees)
    }


    private fun initRects() {
        mCircleRectF[-mCircleWidth, -mCircleHeight, mCircleWidth] = mCircleHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(this.width / 2.toFloat(), this.height / 2.toFloat())
        canvas.drawPath(mCirclePath!!, mCirclePaint!!)
        canvas.drawPath(mCircleProgressPath!!, mCircleProgressGlowPaint!!)
        canvas.drawPath(mCircleProgressPath!!, mCircleProgressPaint!!)
        canvas.drawPath(mCirclePath!!, mCircleFillPaint!!)
        canvas.drawCircle(
            mPointerPositionXY[0],
            mPointerPositionXY[1],
            mPointerRadius + mPointerHaloWidth,
            mPointerHaloPaint!!
        )
        canvas.drawCircle(
            mPointerPositionXY[0],
            mPointerPositionXY[1],
            mPointerRadius,
            mPointerPaint!!
        )
        if (mUserIsMovingPointer) {
            canvas.drawCircle(
                mPointerPositionXY[0],
                mPointerPositionXY[1],
                mPointerRadius + mPointerHaloWidth + mPointerHaloBorderWidth / 2f,
                mPointerHaloBorderPaint!!
            )
        }
    }


    fun setProgress(progress: Int): Unit {
        if (mProgress != progress) {
            mProgress = progress
            if (mOnCircularProgressBarChangeListener != null) {
                mOnCircularProgressBarChangeListener!!.onProgressChanged(this, progress, false)
            }
            recalculateAll()
            invalidate()
        }
    }

    private fun setProgressBasedOnAngle(angle: Float) {
        mPointerPosition = angle
        calculateProgressDegrees()
        mProgress =
            (mMax.toFloat() * mProgressDegrees / mTotalCircleDegrees).roundToInt()
    }

    private fun recalculateAll() {
        calculateTotalDegrees()
        calculatePointerAngle()
        calculateProgressDegrees()
        initRects()
        initPaths()
        calculatePointerXYPosition()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        if (mMaintainEqualCircle) {
            val min = min(width, height)
            setMeasuredDimension(min, min)
        } else {
            setMeasuredDimension(width, height)
        }
        mCircleHeight =
            height.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
        mCircleWidth =
            width.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
        if (mCustomRadii) {
            if (mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleHeight) {
                mCircleHeight =
                    mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }
            if (mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleWidth) {
                mCircleWidth =
                    mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }
        }
        if (mMaintainEqualCircle) {
            val min = min(mCircleHeight, mCircleWidth)
            mCircleHeight = min
            mCircleWidth = min
        }
        recalculateAll()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isTouchEnabled) {
            return false
        }
        val x = event.x - width / 2
        val y = event.y - height / 2
        val distanceX = mCircleRectF.centerX() - x
        val distanceY = mCircleRectF.centerY() - y
        val touchEventRadius = sqrt(
            distanceX.toDouble().pow(2.0) + distanceY.toDouble().pow(2.0)
        ).toFloat()
        val minimumTouchTarget =
            MIN_TOUCH_TARGET_DP * DPTOPX_SCALE
        var additionalRadius: Float
        additionalRadius =
            if (mCircleStrokeWidth < minimumTouchTarget) {
                minimumTouchTarget / 2
            } else {
                mCircleStrokeWidth / 2
            }
        val outerRadius = max(
            mCircleHeight,
            mCircleWidth
        ) + additionalRadius
        val innerRadius = min(
            mCircleHeight,
            mCircleWidth
        ) - additionalRadius
        additionalRadius =
            if (mPointerRadius < minimumTouchTarget / 2) {
                minimumTouchTarget / 2
            } else {
                mPointerRadius
            }
        var touchAngle: Float
        touchAngle = (atan2(
            y.toDouble(),
            x.toDouble()
        ) / Math.PI * 180 % 360).toFloat()
        touchAngle = if (touchAngle < 0) 360 + touchAngle else touchAngle
        cwDistanceFromStart = touchAngle - mStartAngle
        cwDistanceFromStart =
            if (cwDistanceFromStart < 0) 360f + cwDistanceFromStart else cwDistanceFromStart
        ccwDistanceFromStart = 360f - cwDistanceFromStart
        cwDistanceFromEnd = touchAngle - mEndAngle
        cwDistanceFromEnd =
            if (cwDistanceFromEnd < 0) 360f + cwDistanceFromEnd else cwDistanceFromEnd
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val pointerRadiusDegrees =
                    (mPointerRadius * 180 / (Math.PI * max(
                        mCircleHeight,
                        mCircleWidth
                    ))).toFloat()
                cwDistanceFromPointer = touchAngle - mPointerPosition
                cwDistanceFromPointer =
                    if (cwDistanceFromPointer < 0) 360f + cwDistanceFromPointer else cwDistanceFromPointer
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer
                if (touchEventRadius in innerRadius..outerRadius && (cwDistanceFromPointer <= pointerRadiusDegrees || ccwDistanceFromPointer <= pointerRadiusDegrees)) {
                    setProgressBasedOnAngle(mPointerPosition)
                    lastCWDistanceFromStart = cwDistanceFromStart
                    mIsMovingCW = true
                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch
                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch
                    recalculateAll()
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onStartTrackingTouch(this)
                    }
                    mUserIsMovingPointer = true
                    lockAtEnd = false
                    lockAtStart = false
                } else if (cwDistanceFromStart > mTotalCircleDegrees) {
                    mUserIsMovingPointer = false
                    return false
                } else if (touchEventRadius in innerRadius..outerRadius) {
                    setProgressBasedOnAngle(touchAngle)
                    lastCWDistanceFromStart = cwDistanceFromStart
                    mIsMovingCW = true
                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch
                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch
                    recalculateAll()
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onStartTrackingTouch(this)
                        mOnCircularProgressBarChangeListener!!.onProgressChanged(
                            this,
                            mProgress,
                            true
                        )
                    }
                    mUserIsMovingPointer = true
                    lockAtEnd = false
                    lockAtStart = false
                } else {
                    mUserIsMovingPointer = false
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> if (mUserIsMovingPointer) {
                if (lastCWDistanceFromStart < cwDistanceFromStart) {
                    if (cwDistanceFromStart - lastCWDistanceFromStart > 180f && !mIsMovingCW) {
                        lockAtStart = true
                        lockAtEnd = false
                    } else {
                        mIsMovingCW = true
                    }
                } else {
                    if (lastCWDistanceFromStart - cwDistanceFromStart > 180f && mIsMovingCW) {
                        lockAtEnd = true
                        lockAtStart = false
                    } else {
                        mIsMovingCW = false
                    }
                }
                if (lockAtStart && mIsMovingCW) {
                    lockAtStart = false
                }
                if (lockAtEnd && !mIsMovingCW) {
                    lockAtEnd = false
                }
                if (lockAtStart && !mIsMovingCW && ccwDistanceFromStart > 90) {
                    lockAtStart = false
                }
                if (lockAtEnd && mIsMovingCW && cwDistanceFromEnd > 90) {
                    lockAtEnd = false
                }
                if (!lockAtEnd && cwDistanceFromStart > mTotalCircleDegrees && mIsMovingCW && lastCWDistanceFromStart < mTotalCircleDegrees) {
                    lockAtEnd = true
                }
                if (lockAtStart && isLockEnabled) {
                    mProgress = 0
                    recalculateAll()
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onProgressChanged(
                            this,
                            mProgress,
                            true
                        )
                    }
                } else if (lockAtEnd && isLockEnabled) {
                    mProgress = mMax
                    recalculateAll()
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onProgressChanged(
                            this,
                            mProgress,
                            true
                        )
                    }
                } else if (mMoveOutsideCircle || touchEventRadius <= outerRadius) {
                    if (cwDistanceFromStart <= mTotalCircleDegrees) {
                        setProgressBasedOnAngle(touchAngle)
                    }
                    recalculateAll()
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onProgressChanged(
                            this,
                            mProgress,
                            true
                        )
                    }
                }
                lastCWDistanceFromStart = cwDistanceFromStart
            } else {
                return false
            }
            MotionEvent.ACTION_UP -> {
                mPointerHaloPaint!!.alpha = mPointerAlpha
                mPointerHaloPaint!!.color = mPointerHaloColor
                if (mUserIsMovingPointer) {
                    mUserIsMovingPointer = false
                    invalidate()
                    if (mOnCircularProgressBarChangeListener != null) {
                        mOnCircularProgressBarChangeListener!!.onStopTrackingTouch(this)
                    }
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mPointerHaloPaint!!.alpha = mPointerAlpha
                mPointerHaloPaint!!.color = mPointerHaloColor
                mUserIsMovingPointer = false
                invalidate()
            }
        }
        if (event.action == MotionEvent.ACTION_MOVE && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return true
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, defStyle, 0)
        initAttributes(attrArray)
        attrArray.recycle()
        initPaints()
    }

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init(attrs, 0)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable("PARENT", superState)
        state.putInt("MAX", mMax)
        state.putInt("PROGRESS", mProgress)
        state.putInt("mCircleColor", mCircleColor)
        state.putInt("mCircleProgressColor", mCircleProgressColor)
        state.putInt("mPointerColor", mPointerColor)
        state.putInt("mPointerHaloColor", mPointerHaloColor)
        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch)
        state.putInt("mPointerAlpha", mPointerAlpha)
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch)
        state.putBoolean("lockEnabled", isLockEnabled)
        state.putBoolean("isTouchEnabled", isTouchEnabled)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle
        val superState = savedState.getParcelable<Parcelable>("PARENT")
        super.onRestoreInstanceState(superState)
        mMax = savedState.getInt("MAX")
        mProgress = savedState.getInt("PROGRESS")
        mCircleColor = savedState.getInt("mCircleColor")
        mCircleProgressColor = savedState.getInt("mCircleProgressColor")
        mPointerColor = savedState.getInt("mPointerColor")
        mPointerHaloColor = savedState.getInt("mPointerHaloColor")
        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch")
        mPointerAlpha = savedState.getInt("mPointerAlpha")
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch")
        isLockEnabled = savedState.getBoolean("lockEnabled")
        isTouchEnabled = savedState.getBoolean("isTouchEnabled")
        initPaints()
        recalculateAll()
    }


    interface OnCircularProgressBarChangeListener {
        fun onProgressChanged(
            CircularProgressBar: CircularProgressBar?,
            progress: Int,
            fromUser: Boolean
        )

        fun onStopTrackingTouch(seekBar: CircularProgressBar?)
        fun onStartTrackingTouch(seekBar: CircularProgressBar?)
    }


    companion object {
        private const val DEFAULT_CIRCLE_X_RADIUS = 30f
        private const val DEFAULT_CIRCLE_Y_RADIUS = 30f
        private const val DEFAULT_POINTER_RADIUS = 7f
        private const val DEFAULT_POINTER_HALO_WIDTH = 6f
        private const val DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f
        private const val DEFAULT_CIRCLE_STROKE_WIDTH = 5f
        private const val DEFAULT_START_ANGLE = 270f
        private const val DEFAULT_END_ANGLE = 270f
        private const val DEFAULT_MAX = 100
        private const val DEFAULT_PROGRESS = 0
        private const val DEFAULT_CIRCLE_COLOR = Color.DKGRAY
        private val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)
        private val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)
        private val DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255)
        private val DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255)
        private const val DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT
        private const val DEFAULT_POINTER_ALPHA = 0
        private const val DEFAULT_POINTER_ALPHA_ONTOUCH = 0
        private const val DEFAULT_USE_CUSTOM_RADII = false
        private const val DEFAULT_MAINTAIN_EQUAL_CIRCLE = true
        private const val DEFAULT_MOVE_OUTSIDE_CIRCLE = false
        private const val DEFAULT_LOCK_ENABLED = true
    }
}