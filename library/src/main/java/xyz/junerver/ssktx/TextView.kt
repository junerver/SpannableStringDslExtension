package xyz.junerver.ssktx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Size
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

/**
 * Description:
 * @author Junerver
 * date: 2022/3/3-14:47
 * Email: junerver@gmail.com
 * Version: v1.0
 */
interface DslSpannableStringBuilder {
    //增加一段文字
    fun addText(text: String, method: (DslSpanBuilder.() -> Unit)? = null)
}

class DslSpannableStringBuilderImpl(private val textView: TextView) : DslSpannableStringBuilder {
    private val builder = SpannableStringBuilder()

    //记录上次添加文字后最后的索引值
    private var lastIndex: Int = 0
    var isClickable = false

    //内部扩展函数，在这个作用域外无法调用
    fun String.asSpannableString(method: (DslSpanBuilder.() -> Unit)? = null) {
        addText(this, method)
    }

    //重载操作符
    operator fun String.invoke(block: (DslSpanBuilder.() -> Unit)? = null) {
        this.asSpannableString(block)
    }

    override fun addText(text: String, method: (DslSpanBuilder.() -> Unit)?) {
        var addText = text
        val spanBuilder = DslSpanBuilderImpl(textView)
        method?.let { spanBuilder.it() }

        val start = lastIndex
        spanBuilder.imageSpan?.let {
            //添加了图片需要根据左右添加一个空格字符，用于给图片占位
            addText = if (spanBuilder.drawableLeft) " $text" else "$text "
        }
        builder.append(addText)
        lastIndex += addText.length

        spanBuilder.apply {
            if (onClickSpan != null) isClickable = true
            //循环处理所有span
            spanList.forEach {
                builder.setSpan(it, start, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            //图片特殊处理，图片是替换一段文字的占位
            imageSpan?.let {
                if (spanBuilder.drawableLeft) {
                    builder.setSpan(it, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                } else {
                    if (lastIndex >= 1) {
                        builder.setSpan(
                            it,
                            lastIndex - 1,
                            lastIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
    }

    fun build(): SpannableStringBuilder {
        return builder
    }
}

interface DslSpanBuilder {
    //设置文字颜色
    fun setForegroundColor(@Size(min = 1) colorString: String)
    fun setForegroundColor(@ColorRes colorId: Int)
    fun setForegroundColor(@ColorInt color: Int, isRes: Boolean = false)
    //设置文字布局
    fun setAlignment(align: Layout.Alignment)
    //设置文字大小
    fun setTextSize(size: Int)

    //设置文字背景色
    fun setBackgroundColor(@Size(min = 1) colorString: String)
    fun setBackgroundColor(@ColorRes colorId: Int)

    //在文字左边增加图标
    fun setDrawableLeft(
        @DrawableRes drawableId: Int,
        alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE
    )

    fun setDrawableLeft(drawable: Drawable, alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE)

    //在文字右边增加图标
    fun setDrawableRight(
        @DrawableRes drawableId: Int,
        alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE
    )

    fun setDrawableRight(drawable: Drawable, alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE)

    //下划线
    fun userUnderLine()

    //删除线
    fun useStrikethrough()

    //文字作为URLspan
    fun asURL(url: String)

    /**
     * Description: 设置文字样式
     * @author Junerver
     * @param style [Typeface.NORMAL] [Typeface.BOLD] [Typeface.ITALIC] [Typeface.BOLD_ITALIC]
     */
    fun setStyle(style: Int)

    /**
     * Description: 设置点击事件
     * @author Junerver
     * @param useUnderLine 是否为可点击的span添加下划线，默认为 true
     * @param onClick 点击事件
     */
    fun onClick(useUnderLine: Boolean = true, onClick: (View) -> Unit)
}

class DslSpanBuilderImpl(private val textView: TextView) : DslSpanBuilder {
    var foregroundColorSpan: ForegroundColorSpan? = null
    var alignmentSpan: AlignmentSpan? = null
    var textSizeSpan: AbsoluteSizeSpan? = null
    var backgroundColorSpan: BackgroundColorSpan? = null
    var styleSpan: StyleSpan? = null
    var onClickSpan: ClickableSpan? = null
    var imageSpan: ImageSpan? = null
    var underlineSpan: UnderlineSpan? = null
    var strikethroughSpan: StrikethroughSpan? = null
    var urlSpan: URLSpan? = null
    internal val spanList = mutableListOf<Any?>()

    //添加的 drawable 默认位于文字左侧
    var drawableLeft = true

    override fun setForegroundColor(@Size(min = 1) colorString: String) {
        foregroundColorSpan = ForegroundColorSpan(Color.parseColor(colorString))
        spanList.add(foregroundColorSpan)
    }

    override fun setForegroundColor(@ColorRes colorId: Int) {
        foregroundColorSpan = ForegroundColorSpan(textView.context.getColorRes(colorId))
        spanList.add(foregroundColorSpan)
    }

    override fun setForegroundColor(@ColorInt color: Int, isRes: Boolean) {
        foregroundColorSpan = ForegroundColorSpan(color)
        spanList.add(foregroundColorSpan)
    }

    override fun setAlignment(align: Layout.Alignment) {
        alignmentSpan = AlignmentSpan.Standard(align)
        spanList.add(alignmentSpan)
    }

    override fun setTextSize(size: Int) {
        textSizeSpan = AbsoluteSizeSpan(size)
        spanList.add(textSizeSpan)
    }

    override fun setBackgroundColor(@Size(min = 1) colorString: String) {
        backgroundColorSpan = BackgroundColorSpan(Color.parseColor(colorString))
        spanList.add(backgroundColorSpan)
    }

    override fun setBackgroundColor(@ColorRes colorId: Int) {
        backgroundColorSpan = BackgroundColorSpan(textView.context.getColorRes(colorId))
        spanList.add(backgroundColorSpan)
    }

    private fun setDrawable(
        drawableId: Int,
        left: Boolean = true,
        alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE
    ) {
        drawableLeft = left
        val drawable: Drawable = textView.context.getDrawableRes(drawableId)
        drawable.setBounds(0, 0, textView.lineHeight, textView.lineHeight)
        imageSpan = ImageSpan(drawable, alignment)
//        imageSpan = VerticalImageSpan(textView.context,drawableId, alignment)
    }

    private fun setDrawable(
        drawable: Drawable,
        left: Boolean = true,
        alignment: Int = DynamicDrawableSpan.ALIGN_BASELINE
    ) {
        drawableLeft = left
        imageSpan = ImageSpan(drawable, alignment)
    }

    override fun setDrawableLeft(drawableId: Int, alignment: Int) {
        setDrawable(drawableId, alignment = alignment)
    }

    override fun setDrawableLeft(drawable: Drawable, alignment: Int) {
        setDrawable(drawable, alignment = alignment)
    }

    override fun setDrawableRight(drawableId: Int, alignment: Int) {
        setDrawable(drawableId, false, alignment)
    }

    override fun setDrawableRight(drawable: Drawable, alignment: Int) {
        setDrawable(drawable, false, alignment)
    }

    override fun userUnderLine() {
        underlineSpan = UnderlineSpan()
        spanList.add(underlineSpan)
    }

    override fun useStrikethrough() {
        strikethroughSpan = StrikethroughSpan()
        spanList.add(strikethroughSpan)
    }

    override fun asURL(url: String) {
        urlSpan = URLSpan(url)
        spanList.add(urlSpan)
    }

    override fun setStyle(style: Int) {
        if (style in IntRange(Typeface.NORMAL,Typeface.BOLD_ITALIC)) {
            styleSpan = StyleSpan(style)
            spanList.add(styleSpan)
        }
    }

    override fun onClick(useUnderLine: Boolean, onClick: (View) -> Unit) {
        onClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick(widget)
            }
        }
        spanList.add(onClickSpan)
        if (!useUnderLine) {
            spanList.add(NoUnderlineSpan())
        }
    }
}

class NoUnderlineSpan : UnderlineSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.isUnderlineText = false
    }
}

class VerticalImageSpan(context: Context, resourceId: Int, verticalAlignment: Int) :
    ImageSpan(context, resourceId, verticalAlignment) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b = drawable
        canvas.save()
        var transY = 0
        //获得将要显示的文本高度 - 图片高度除2 = 居中位置+top(换行情况)
        transY = (bottom - top - b.bounds.bottom) / 2 + top
        //偏移画布后开始绘制
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        val d = drawable
        val rect = d.bounds
        if (fm != null) {
            val fmPaint = paint.fontMetricsInt
            //获得文字、图片高度
            val fontHeight = fmPaint.bottom - fmPaint.top
            val drHeight = rect.bottom - rect.top
            val top = drHeight / 2 - fontHeight / 4
            val bottom = drHeight / 2 + fontHeight / 4
            fm.ascent = -bottom
            fm.top = -bottom
            fm.bottom = top
            fm.descent = top
        }
        return rect.right
    }
}

fun TextView.buildSpannableString(init: DslSpannableStringBuilderImpl.() -> Unit) {
    val spanStringBuilderImpl = DslSpannableStringBuilderImpl(this)
    spanStringBuilderImpl.init()
    if (spanStringBuilderImpl.isClickable) {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = context.getColorRes(android.R.color.transparent)
    }
    text = spanStringBuilderImpl.build()
}

fun Context.getDrawableRes(@DrawableRes id: Int): Drawable {
    return AppCompatResources.getDrawable(this, id)!!
}

fun Context.getColorRes(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}