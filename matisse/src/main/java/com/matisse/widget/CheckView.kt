package com.matisse.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.matisse.R

/**
 * Created by liubo on 2018/9/4.
 */
class CheckView : View {

    companion object {
        const val UNCHECKED = Integer.MIN_VALUE
        private const val STROKE_WIDTH = 1.5f           // 圆环宽度
        private const val SHADOW_WIDTH = 6.0f           // 阴影宽度
        private const val SIZE = 30
        private const val STROKE_RADIUS = 12.5f
        private const val BG_RADIUS = 13.5f
        private const val CONTENT_SIZE = 18
    }

    private var countable = false
    private var checked = false
    private var checkedNum = 0
    private var strokePaint: Paint? = null
    private var backgroundPaint: Paint? = null
    private var textPaint: Paint? = null
    private var shadowPaint: Paint? = null
    private var checkDrawable: Drawable? = null
    private var kdensity = 0f
    private var checkRect: Rect? = null
    private var enable = true
    private var halfDensitySize = 0f

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initParams()
    }

    private fun initParams() {
        kdensity = context.resources?.displayMetrics?.density ?: 0f
        halfDensitySize = kdensity * SIZE / 2f

        strokePaint = Paint().run {
            isAntiAlias = true
            style = Paint.Style.STROKE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            strokeWidth = STROKE_WIDTH * kdensity
            this
        }

        val ta: TypedArray =
            context.theme.obtainStyledAttributes(intArrayOf(R.attr.Item_checkCircle_borderColor))
        val defaultColor = ResourcesCompat.getColor(
            context.resources, R.color.item_checkCircle_borderColor, context.theme
        )
        val color = ta.getColor(0, defaultColor)
        ta.recycle()
        strokePaint?.color = Color.WHITE//color

        checkDrawable = ResourcesCompat.getDrawable(
            context.resources, R.drawable.ic_check_white_18dp, context.theme
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeSpec = MeasureSpec.makeMeasureSpec((kdensity * SIZE).toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(sizeSpec, sizeSpec)
    }

    fun setEnable(enable: Boolean) {
        if (this.enable != enable) {
            this.enable = enable
            invalidate()
        }
    }

    fun setCountable(boolean: Boolean) {
        if (countable != boolean) {
            countable = boolean
            invalidate()
        }
    }

    fun setChecked(boolean: Boolean) {
        if (countable) {
            throw IllegalStateException("CheckView is countable, call setCheckedNum() instead.")
        }

        checked = boolean
        invalidate()
    }

    fun setCheckedNum(num: Int) {
        if (!countable) {
            throw IllegalStateException("CheckView is not countable, call setChecked() instead.")
        }
        if (num != UNCHECKED && num < 0) {
            throw  IllegalStateException("the num can't be negative")
        }
        checkedNum = num
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.setDrawFilter(PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        val bitmapOn = BitmapFactory.decodeResource(resources,R.drawable.ic_preview_radio_on2);
        val bitmapOff = BitmapFactory.decodeResource(resources,R.drawable.ic_preview_radio_off);
        // 指定图片在屏幕上显示的区域
        val srcRect = Rect(0, 0, bitmapOff.width, bitmapOff.height)
        // 指定图片在屏幕上显示的区域
        val dstRect = Rect(0, 0, width, height)
        // draw content

        if (countable) {
            if (checkedNum != UNCHECKED) {
//                setBackgroundResource(R.drawable.ic_preview_radio_on)



                Paint().apply {
                    canvas?.drawBitmap(bitmapOn, srcRect,dstRect,null)
                }
                initBackgroundPaint()
                backgroundPaint?.apply {
                    canvas?.drawCircle(
                        halfDensitySize, halfDensitySize, kdensity.times(BG_RADIUS), this
                    )
                }
                initTextPaint()
                textPaint?.apply {
                    val text = checkedNum.toString()
                    val baseX = (width - measureText(text)) / 2
                    val baseY = (height - descent() - ascent()) / 2
                    canvas?.drawText(text, baseX, baseY, this)
                }
            }else{
                Paint().apply {
                    canvas?.drawBitmap(bitmapOff, srcRect,dstRect,this)
                }
            }
        } else {
            Paint().apply {
                if (checked) {
                    canvas?.drawBitmap(bitmapOn, srcRect,dstRect,this)
                }else{
                    canvas?.drawBitmap(bitmapOff, srcRect,dstRect,this)
                }
            }
        }
        bitmapOn.recycle()
        bitmapOff.recycle()

        // draw white stroke
        strokePaint?.apply {
            canvas?.drawCircle(
                    halfDensitySize, halfDensitySize, kdensity.times(STROKE_RADIUS+STROKE_WIDTH), this
            )
        }


        alpha = if (enable) 1.0f else 0.5f
    }

    private fun getCheckRect(): Rect {
        if (checkRect == null) {
            val rectPadding = (halfDensitySize - CONTENT_SIZE * kdensity / 2).toInt()
            checkRect = Rect(
                rectPadding, rectPadding,
                (SIZE * kdensity - rectPadding).toInt(), (SIZE * kdensity - rectPadding).toInt()
            )
        }
        return checkRect!!
    }

    private fun initTextPaint() {
        if (textPaint == null) {
            textPaint = TextPaint().run {
                isAntiAlias = true
                color = Color.WHITE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textSize = 14.0f * kdensity
                this
            }
        }
    }

    private fun initBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = Paint()
            backgroundPaint?.isAntiAlias = true
            backgroundPaint?.style = Paint.Style.FILL
            val ta: TypedArray =
                    context.theme.obtainStyledAttributes(intArrayOf(R.attr.Item_checkCircle_bgColor))
            val defaultColor = ResourcesCompat.getColor(
                    context.resources, R.color.selector_base_text, context.theme
            )
            val color = ta.getColor(0, defaultColor)
            ta.recycle()
            backgroundPaint?.color = color


        }
    }

    private fun initShadowPaint() {
        if (shadowPaint == null) {
            shadowPaint = Paint()
            shadowPaint?.isAntiAlias = true
            val outerRadius: Float = STROKE_RADIUS + STROKE_WIDTH / 2
            val innerRadius = outerRadius - STROKE_WIDTH
            val gradientRadius = outerRadius + SHADOW_WIDTH
            val stop0 = (innerRadius - STROKE_WIDTH) / gradientRadius
            val stop1 = innerRadius / gradientRadius
            val stop2 = outerRadius / gradientRadius
            val stop3 = 1f

            val shadow = ContextCompat.getColor(context, R.color.shadow)
            val shadowHint = ContextCompat.getColor(context, R.color.shadow_hint)
            shadowPaint?.shader = (RadialGradient(
                halfDensitySize, halfDensitySize, kdensity.times(gradientRadius),
                intArrayOf(shadowHint, shadow, shadow, shadowHint),
                floatArrayOf(stop0, stop1, stop2, stop3), Shader.TileMode.CLAMP
            ))
        }
    }
}