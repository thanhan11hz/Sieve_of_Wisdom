package com.example.sieve_of_wisdom.ui.neo

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.example.sieve_of_wisdom.R
import androidx.core.content.res.ResourcesCompat
class NeoTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // =========================================================
    // INTERNAL TEXT VIEWS
    // =========================================================

    private lateinit var shadowTextView: TextView
    private lateinit var mainTextView: TextView
    private lateinit var strokeTextView: TextView


    // =========================================================
    // NEO SHADOW
    // =========================================================

    private var shadowColor: Int = Color.rgb(255, 212, 42)

    private var shadowX: Float = dpToPx(3f)

    private var shadowY: Float = dpToPx(3f)


    // =========================================================
    // TEXT STROKE
    // =========================================================

    private var strokeColor: Int = Color.BLACK

    private var strokeWidth: Float = dpToPx(1.5f)


    // =========================================================
    // TEXT ATTRIBUTES
    // =========================================================

    private var neoText: CharSequence? = ""

    private var neoTextColor: Int = Color.BLACK

    private var neoTextSize: Float = dpToPx(16f)

    private var neoTextStyle: Int = Typeface.NORMAL

    private var neoTypeface: Typeface = Typeface.DEFAULT

    private var neoGravity: Int =
        Gravity.START or Gravity.CENTER_VERTICAL


    // =========================================================
    // INIT
    // =========================================================

    init {

        // Cho phép shadow nằm lệch ra ngoài component
        clipChildren = false
        clipToPadding = false

        // Đọc tất cả XML attributes
        readAttributes(context, attrs)

        // Tạo Main + Shadow TextView
        createTextViews(context)

        // Apply dữ liệu
        applyAttributes()
    }


    // =========================================================
    // READ XML ATTRIBUTES
    // =========================================================

    private fun readAttributes(
        context: Context,
        attrs: AttributeSet?
    ) {

        if (attrs == null) {
            return
        }

        val typedArray: TypedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.NeoTextView
            )


        // =====================================================
        // NEO SHADOW
        // =====================================================

        shadowColor = typedArray.getColor(
            R.styleable.NeoTextView_neoShadowColor,
            shadowColor
        )

        shadowX = typedArray.getDimension(
            R.styleable.NeoTextView_neoShadowX,
            shadowX
        )

        shadowY = typedArray.getDimension(
            R.styleable.NeoTextView_neoShadowY,
            shadowY
        )


        // =====================================================
        // STROKE
        // =====================================================

        strokeColor = typedArray.getColor(
            R.styleable.NeoTextView_neoStrokeColor,
            strokeColor
        )

        strokeWidth = typedArray.getDimension(
            R.styleable.NeoTextView_neoStrokeWidth,
            strokeWidth
        )


        // =====================================================
        // TEXT
        // =====================================================

        neoText = typedArray.getText(
            R.styleable.NeoTextView_android_text
        )


        // =====================================================
        // TEXT COLOR
        // =====================================================

        neoTextColor = typedArray.getColor(
            R.styleable.NeoTextView_android_textColor,
            Color.BLACK
        )


        // =====================================================
        // TEXT SIZE
        // =====================================================

        neoTextSize = typedArray.getDimension(
            R.styleable.NeoTextView_android_textSize,
            dpToPx(16f)
        )


        // =====================================================
        // TEXT STYLE
        // =====================================================

        neoTextStyle = typedArray.getInt(
            R.styleable.NeoTextView_android_textStyle,
            Typeface.NORMAL
        )


        // =====================================================
        // FONT FAMILY
        // =====================================================

        val fontId = typedArray.getResourceId(
            R.styleable.NeoTextView_android_fontFamily,
            0
        )

        if (fontId != 0) {

            try {
                neoTypeface = ResourcesCompat.getFont(context, fontId)
                    ?: Typeface.DEFAULT
            } catch (_: Exception) {
                neoTypeface = Typeface.DEFAULT
            }
        }


        // =====================================================
        // GRAVITY
        // =====================================================

        neoGravity = typedArray.getInt(
            R.styleable.NeoTextView_android_gravity,
            Gravity.START or Gravity.CENTER_VERTICAL
        )


        // =====================================================
        // RELEASE TYPED ARRAY
        // =====================================================

        typedArray.recycle()
    }


    // =========================================================
    // CREATE TEXT VIEWS
    // =========================================================

    private fun createTextViews(context: Context) {

        // =====================================================
        // 1. SHADOW
        // =====================================================

        shadowTextView = TextView(context)

        shadowTextView.gravity = neoGravity
        shadowTextView.includeFontPadding = true
        shadowTextView.setTextColor(shadowColor)

        val shadowParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        shadowParams.leftMargin = shadowX.toInt()
        shadowParams.topMargin = shadowY.toInt()

        addView(
            shadowTextView,
            shadowParams
        )


        // =====================================================
        // 2. STROKE
        // =====================================================

        strokeTextView = TextView(context)

        strokeTextView.gravity = neoGravity
        strokeTextView.includeFontPadding = true

        // Stroke màu đen
        strokeTextView.setTextColor(strokeColor)

        val strokePaint = strokeTextView.paint

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth
        strokePaint.strokeJoin = Paint.Join.ROUND

        val strokeParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        addView(
            strokeTextView,
            strokeParams
        )


        // =====================================================
        // 3. MAIN TEXT
        // =====================================================

        mainTextView = TextView(context)

        mainTextView.gravity = neoGravity
        mainTextView.includeFontPadding = true

        // Fill màu chữ
        mainTextView.setTextColor(neoTextColor)

        val mainParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        addView(
            mainTextView,
            mainParams
        )
    }


    // =========================================================
    // APPLY ATTRIBUTES
    // =========================================================

    private fun applyAttributes() {

        // =====================================================
        // TEXT
        // =====================================================

        mainTextView.text = neoText
        strokeTextView.text = neoText
        shadowTextView.text = neoText


        // =====================================================
        // COLORS
        // =====================================================

        mainTextView.setTextColor(neoTextColor)

        strokeTextView.setTextColor(strokeColor)

        shadowTextView.setTextColor(shadowColor)


        // =====================================================
        // SIZE
        // =====================================================

        mainTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            neoTextSize
        )

        strokeTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            neoTextSize
        )

        shadowTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            neoTextSize
        )


        // =====================================================
        // TYPEFACE
        // =====================================================

        val finalTypeface = Typeface.create(
            neoTypeface,
            neoTextStyle
        )

        mainTextView.typeface = finalTypeface
        strokeTextView.typeface = finalTypeface
        shadowTextView.typeface = finalTypeface


        // =====================================================
        // GRAVITY
        // =====================================================

        mainTextView.gravity = neoGravity
        strokeTextView.gravity = neoGravity
        shadowTextView.gravity = neoGravity


        // =====================================================
        // STROKE
        // =====================================================

        val strokePaint = strokeTextView.paint

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth
        strokePaint.strokeJoin = Paint.Join.ROUND

        strokeTextView.invalidate()
    }


    // =========================================================
    // STROKE
    // =========================================================

    private fun applyStroke() {

        val paint: Paint = mainTextView.paint

        paint.style = Paint.Style.FILL_AND_STROKE

        paint.strokeWidth = strokeWidth

        paint.strokeJoin = Paint.Join.ROUND

        paint.strokeCap = Paint.Cap.ROUND

        /*
         * Paint của TextView dùng màu text hiện tại
         * làm màu fill.
         */
        paint.color = neoTextColor

        mainTextView.invalidate()
    }


    // =========================================================
    // SET TEXT
    // =========================================================

    fun setText(text: CharSequence?) {

        neoText = text

        if (::mainTextView.isInitialized) {

            mainTextView.text = text
            strokeTextView.text = text
            shadowTextView.text = text
        }
    }


    fun getText(): CharSequence? {

        return if (::mainTextView.isInitialized) {
            mainTextView.text
        } else {
            neoText
        }
    }


    // =========================================================
    // SET TEXT COLOR
    // =========================================================

    fun setNeoTextColor(color: Int) {

        neoTextColor = color

        mainTextView.setTextColor(color)

        mainTextView.invalidate()
    }


    // =========================================================
    // SET TEXT SIZE
    // =========================================================

    fun setNeoTextSize(sizeSp: Float) {

        mainTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp
        )

        shadowTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp
        )

        invalidate()
    }


    // =========================================================
    // SET TYPEFACE
    // =========================================================

    fun setNeoTypeface(typeface: Typeface) {

        neoTypeface = typeface

        mainTextView.typeface = typeface

        shadowTextView.typeface = typeface

        invalidate()
    }


    // =========================================================
    // SET SHADOW COLOR
    // =========================================================

    fun setNeoShadowColor(color: Int) {

        shadowColor = color

        shadowTextView.setTextColor(color)

        shadowTextView.invalidate()
    }


    // =========================================================
    // SET SHADOW OFFSET
    // =========================================================

    fun setNeoShadowOffset(
        xDp: Float,
        yDp: Float
    ) {

        shadowX = dpToPx(xDp)

        shadowY = dpToPx(yDp)


        val params =
            shadowTextView.layoutParams as LayoutParams

        params.leftMargin = shadowX.toInt()

        params.topMargin = shadowY.toInt()


        shadowTextView.layoutParams = params

        invalidate()
    }


    // =========================================================
    // SET STROKE COLOR
    // =========================================================

    fun setNeoStrokeColor(color: Int) {

        strokeColor = color

        strokeTextView.setTextColor(color)

        strokeTextView.invalidate()
    }


    // =========================================================
    // SET STROKE WIDTH
    // =========================================================

    fun setNeoStrokeWidth(widthDp: Float) {

        strokeWidth = dpToPx(widthDp)

        val paint = strokeTextView.paint

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeJoin = Paint.Join.ROUND

        strokeTextView.invalidate()
    }


    // =========================================================
    // DP → PX
    // =========================================================

    private fun dpToPx(dp: Float): Float {

        return dp *
                resources.displayMetrics.density
    }
}