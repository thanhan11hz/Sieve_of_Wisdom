package com.example.sieve_of_wisdom.ui.neo

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

import com.example.sieve_of_wisdom.R


class NeoFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // =========================================================
    // NEO PROPERTIES
    // =========================================================

    private var neoShadowColor = Color.BLACK
    private var neoShadowX = dpToPx(4f)
    private var neoShadowY = dpToPx(5f)

    private var neoStrokeColor = Color.BLACK
    private var neoStrokeWidth = dpToPx(2f)

    private var neoBackgroundColor = Color.WHITE
    private var neoCornerRadius = dpToPx(12f)

    private lateinit var shadowView: View
    private lateinit var contentContainer: FrameLayout


    init {

        setClipChildren(false)
        setClipToPadding(false)

        readAttributes(context, attrs)

        createNeoLayers()
    }


    // =========================================================
    // READ XML ATTRIBUTES
    // =========================================================

    private fun readAttributes(
        context: Context,
        attrs: AttributeSet?
    ) {

        if (attrs == null) return

        val typedArray: TypedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.NeoFrameLayout
            )

        neoShadowColor = typedArray.getColor(
            R.styleable.NeoFrameLayout_neoShadowColor,
            neoShadowColor
        )

        neoShadowX = typedArray.getDimension(
            R.styleable.NeoFrameLayout_neoShadowX,
            neoShadowX
        )

        neoShadowY = typedArray.getDimension(
            R.styleable.NeoFrameLayout_neoShadowY,
            neoShadowY
        )

        neoStrokeColor = typedArray.getColor(
            R.styleable.NeoFrameLayout_neoStrokeColor,
            neoStrokeColor
        )

        neoStrokeWidth = typedArray.getDimension(
            R.styleable.NeoFrameLayout_neoStrokeWidth,
            neoStrokeWidth
        )

        neoBackgroundColor = typedArray.getColor(
            R.styleable.NeoFrameLayout_neoBackgroundColor,
            neoBackgroundColor
        )

        neoCornerRadius = typedArray.getDimension(
            R.styleable.NeoFrameLayout_neoCornerRadius,
            neoCornerRadius
        )

        typedArray.recycle()
    }


    // =========================================================
    // CREATE LAYERS
    // =========================================================

    private fun createNeoLayers() {


        shadowView = View(context)

        contentContainer = FrameLayout(context)

        contentContainer.setClipChildren(false)
        contentContainer.setClipToPadding(false)


        // =====================================================
        // SHADOW
        // =====================================================

        shadowView.background = createBackground(
            fillColor = neoShadowColor,
            strokeColor = neoShadowColor,
            strokeWidth = 0f
        )


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        contentContainer.background = createBackground(
            fillColor = neoBackgroundColor,
            strokeColor = neoStrokeColor,
            strokeWidth = neoStrokeWidth
        )


        // =====================================================
        // ADD SHADOW (FULL SIZE)
        // =====================================================

        addView(
            shadowView,
            createShadowLayoutParams()
        )


        // =====================================================
        // ADD CONTENT (SHRINK BY MARGIN)
        // =====================================================

        val contentParams = createContentLayoutParams()

        // Margin trên contentContainer để lộ shadow ở phía dưới-phải
        contentParams.rightMargin = neoShadowX.toInt()
        contentParams.bottomMargin = neoShadowY.toInt()

        addView(
            contentContainer,
            contentParams
        )
    }


    // =========================================================
    // LAYOUT PARAMS
    // =========================================================

    private fun createShadowLayoutParams(): LayoutParams {

        // shadowView fill toàn bộ parent, không có margin
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }


    private fun createContentLayoutParams(): LayoutParams {

        // contentContainer MATCH_PARENT nhưng margin sẽ được thêm sau
        // để lộ shadow ở phía dưới-phải
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }


    // =========================================================
    // BACKGROUND
    // =========================================================

    private fun createBackground(
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Float
    ): GradientDrawable {

        return GradientDrawable().apply {

            shape = GradientDrawable.RECTANGLE

            setColor(fillColor)

            if (strokeWidth > 0f) {
                setStroke(
                    strokeWidth.toInt(),
                    strokeColor
                )
            }

            cornerRadius = neoCornerRadius
        }
    }


    // =========================================================
    // IMPORTANT:
    // PUT CHILD VIEWS INSIDE CONTENT CONTAINER
    // =========================================================

    override fun addView(
        child: View?,
        index: Int,
        params: android.view.ViewGroup.LayoutParams?
    ) {

        /*
         * Không cho content đi trực tiếp vào root.
         *
         * Root chỉ quản lý:
         *
         * Shadow
         * Content container
         */

        if (
            ::contentContainer.isInitialized &&
            child !== shadowView &&
            child !== contentContainer
        ) {

            contentContainer.addView(
                child,
                index,
                params
            )

        } else {

            super.addView(
                child,
                index,
                params
            )
        }
    }


    // =========================================================
    // SETTERS
    // =========================================================

    fun setNeoShadowColor(color: Int) {

        neoShadowColor = color

        shadowView.background = createBackground(
            fillColor = neoShadowColor,
            strokeColor = neoShadowColor,
            strokeWidth = 0f
        )

        invalidate()
    }


    fun setNeoShadowOffset(
        xDp: Float,
        yDp: Float
    ) {

        neoShadowX = dpToPx(xDp)
        neoShadowY = dpToPx(yDp)

        // Cập nhật margin của contentContainer để lộ shadow
        val params = contentContainer.layoutParams as LayoutParams
        params.rightMargin = neoShadowX.toInt()
        params.bottomMargin = neoShadowY.toInt()
        contentContainer.layoutParams = params

        invalidate()
    }


    fun setNeoStrokeColor(color: Int) {

        neoStrokeColor = color

        updateContentBackground()
    }


    fun setNeoStrokeWidth(widthDp: Float) {

        neoStrokeWidth = dpToPx(widthDp)

        updateContentBackground()
    }


    fun setNeoBackgroundColor(color: Int) {

        neoBackgroundColor = color

        updateContentBackground()
    }


    fun setNeoCornerRadius(radiusDp: Float) {

        neoCornerRadius = dpToPx(radiusDp)

        updateContentBackground()

        shadowView.background = createBackground(
            fillColor = neoShadowColor,
            strokeColor = neoShadowColor,
            strokeWidth = 0f
        )
    }


    // =========================================================
    // UPDATE MAIN CONTENT
    // =========================================================

    private fun updateContentBackground() {

        contentContainer.background = createBackground(
            fillColor = neoBackgroundColor,
            strokeColor = neoStrokeColor,
            strokeWidth = neoStrokeWidth
        )
    }


    // =========================================================
    // UTILITY
    // =========================================================

    private fun dpToPx(dp: Float): Float {

        return dp *
                resources.displayMetrics.density
    }
}