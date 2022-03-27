package com.github.zicheng.chart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.OverScroller
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.github.zicheng.chart.ext.dp
import com.github.zicheng.chart.ext.scale
import timber.log.Timber
import kotlin.math.abs

class LineChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet?,
                                              defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var showXAxis = true
    private var xAxisColor = Color.GRAY
    private var xAxisWidth = 1.dp
    private var showXText = true
    private var xTextColor = 0
    private var xTextSize = 10.dp
    private var showXScaleLine = true
    private var xScaleLineColor = 0
    private var xScaleLineWidth = 1.dp
    private var xScaleLineLength = 4.dp

    private var showYAxis = true
    private var yAxisColor = Color.GRAY
    private var yAxisWidth = 1.dp
    private var showYText = true
    private var yTextColor = 0
    private var yTextSize = 10.dp
    private var yTextAlign = TextAlign.LEFT
    private var showYScaleLine = true
    private var yScaleLineColor = 0
    private var yScaleLineWidth = 1.dp
    private var yScaleLineLength = 4.dp

    private var showAxisArrow = true
    private var axisArrowWidth = 6.dp
    private var axisArrowHeight = 3.dp
    private var axisArrowColor = Color.GRAY

    private var limitLineColor = Color.GRAY
    private var limitLineWidth = 1.dp
    private var limitLineLength = 2.dp
    private var limitLineSpace = 2.dp

    private var lineChartWidth = 1.5f.dp
    private var lineChartColor = Color.LTGRAY
    private var lineChartPaddingTop = 30.dp.toInt()
    private var lineChartPaddingBottom = 15.dp.toInt()
    private var lineChartPaddingStart = 30.dp.toInt()
    private var lineChartBgColor = Color.WHITE
    private var drawCurve = false//绘制曲线
    private var showLineChartAnim = false

    private var showLineChartPoint = true
    private var pointRadius = 3f.dp
    private var pointColor = Color.WHITE
    private var pointStrokeWidth = 0f
    private var pointStrokeColor = Color.WHITE
    private var pointSelectedRadius = 0f
    private var pointSelectedColor = 0
    private var pointSelectedStrokeWidth = 0f
    private var pointSelectedStrokeColor = 0
    private var pointSelectedOutStrokeWidth = 0f
    private var pointSelectedOutStrokeColor = Color.parseColor("#99FFFFFF")
    private var pointStartX = 0
    private var pointEndX = 0

    private var showPointFloatBox = true
    private var floatBoxPadding = 4.dp
    private var floatBoxColor = Color.WHITE
    private var floatBoxTextColor = Color.GRAY
    private var floatBoxTextSize = 12.dp

    private lateinit var linePaint: Paint
    private lateinit var lineChartPaint: Paint
    private lateinit var limitPaint: Paint
    private lateinit var pointPaint: Paint
    private lateinit var textPaint: TextPaint

    //折线点对应的数据
    private var pointList: List<PointInfo> = ArrayList()
    private var limitArray: List<Int>? = null
    //原点坐标x
    private var originX = 0f
    //原点坐标y
    private var originY = 0f
    //折线点间距
    private var pointSpace = 0f
    private var drawWidth = 0f
    private var drawHeight = 0f
    private var slideX = 0f
    private var maxSlideX = 0f
    private var minSlideX = 0f
    private var selectedIndex = -1
    private var xMin = 0f
    private var xMax = 0f
    //x轴坐标对应的数据
    private var xAxisList: List<AxisInfo> = ArrayList()
        set(value) {
            field = value
            xMin = field.first().value
            xMax = field.last().value
        }
    private var yMin = 0f
    private var yMax = 0f
    //y轴坐标对应的数据
    private var yAxisList: List<AxisInfo> = ArrayList()
        set(value) {
            field = value
            yMin = field.first().value
            yMax = field.last().value
        }
    private var lineChartPath = Path()
    private var limitLinePath: Path? = null
    private var textBoxPath: Path? = null
    private var xAxisArrowPath: Path? = null
    private var yAxisArrowPath: Path? = null
    private var saveRect = RectF()
    private var clearRect = RectF()
    private var xTextHeight = 0
    private var yTextHeight = 0

    init {
        if(attrs != null){
            initCustomAttrs(context, attrs)
        }
        initPaint()
        val textRect = Rect()
        textPaint.textSize = xTextSize
        val text = "TEXT"
        textPaint.getTextBounds(text, 0, text.length, textRect)
        xTextHeight = textRect.height()
        textPaint.textSize = yTextSize
        textPaint.getTextBounds(text, 0, text.length, textRect)
        yTextHeight = textRect.height()
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LineChartView)
        showXAxis = ta.getBoolean(R.styleable.LineChartView_showXAxis, showXAxis)
        xAxisColor = ta.getColor(R.styleable.LineChartView_xAxisColor, xAxisColor)
        xAxisWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_xAxisWidth, xAxisWidth.toInt()).toFloat()
        showXText = ta.getBoolean(R.styleable.LineChartView_showXText, showXText)
        xTextColor = ta.getColor(R.styleable.LineChartView_xTextColor, xAxisColor)
        xTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_xTextSize, xTextSize.toInt()).toFloat()
        showXScaleLine = ta.getBoolean(R.styleable.LineChartView_showXScaleLine, showXScaleLine)
        xScaleLineColor = ta.getColor(R.styleable.LineChartView_xScaleLineColor, xAxisColor)
        xScaleLineWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_xScaleLineWidth, xScaleLineWidth.toInt()).toFloat()
        xScaleLineLength = ta.getDimensionPixelSize(R.styleable.LineChartView_xScaleLineLength, xScaleLineLength.toInt()).toFloat()

        showYAxis = ta.getBoolean(R.styleable.LineChartView_showYAxis, showYAxis)
        yAxisColor = ta.getColor(R.styleable.LineChartView_yAxisColor, yAxisColor)
        yAxisWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_yAxisWidth, yAxisWidth.toInt()).toFloat()
        showYText = ta.getBoolean(R.styleable.LineChartView_showYText, showYText)
        yTextColor = ta.getColor(R.styleable.LineChartView_yTextColor, yAxisColor)
        yTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_yTextSize, yTextSize.toInt()).toFloat()
        val ordinal = ta.getInt(R.styleable.LineChartView_yTextAlign, yTextAlign.ordinal)
        yTextAlign = TextAlign.values()[ordinal]
        showYScaleLine = ta.getBoolean(R.styleable.LineChartView_showYScaleLine, showYScaleLine)
        yScaleLineColor = ta.getColor(R.styleable.LineChartView_yScaleLineColor, yAxisColor)
        yScaleLineWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_yScaleLineWidth, yScaleLineWidth.toInt()).toFloat()
        yScaleLineLength = ta.getDimensionPixelSize(R.styleable.LineChartView_yScaleLineLength, yScaleLineLength.toInt()).toFloat()

        showAxisArrow = ta.getBoolean(R.styleable.LineChartView_showAxisArrow, showAxisArrow)
        axisArrowWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_axisArrowWidth, axisArrowWidth.toInt()).toFloat()
        axisArrowHeight = ta.getDimensionPixelSize(R.styleable.LineChartView_axisArrowHeight, axisArrowHeight.toInt()).toFloat()
        axisArrowColor = ta.getColor(R.styleable.LineChartView_axisArrowColor, axisArrowColor)

        limitLineColor = ta.getColor(R.styleable.LineChartView_limitLineColor, limitLineColor)
        limitLineWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_limitLineWidth, limitLineWidth.toInt()).toFloat()
        limitLineLength = ta.getDimensionPixelSize(R.styleable.LineChartView_limitLineLength, limitLineLength.toInt()).toFloat()
        limitLineSpace = ta.getDimensionPixelSize(R.styleable.LineChartView_limitLineSpace, limitLineSpace.toInt()).toFloat()

        lineChartColor = ta.getColor(R.styleable.LineChartView_lineChartColor, lineChartColor)
        lineChartWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_lineChartWidth, lineChartWidth.toInt()).toFloat()
        lineChartPaddingTop = ta.getDimensionPixelSize(R.styleable.LineChartView_lineChartPaddingTop, lineChartPaddingTop)
        lineChartPaddingBottom = ta.getDimensionPixelSize(R.styleable.LineChartView_lineChartPaddingBottom, lineChartPaddingBottom)
        lineChartPaddingStart = ta.getDimensionPixelSize(R.styleable.LineChartView_lineChartPaddingStart, lineChartPaddingStart)
        lineChartBgColor = ta.getColor(R.styleable.LineChartView_lineChartBgColor, lineChartBgColor)
        drawCurve = ta.getBoolean(R.styleable.LineChartView_drawCurve, drawCurve)
        showLineChartAnim = ta.getBoolean(R.styleable.LineChartView_showLineChartAnim, showLineChartAnim)

        showLineChartPoint = ta.getBoolean(R.styleable.LineChartView_showLineChartPoint, showLineChartPoint)
        pointColor = ta.getColor(R.styleable.LineChartView_pointColor, pointColor)
        pointRadius = ta.getDimensionPixelSize(R.styleable.LineChartView_pointRadius, pointRadius.toInt()).toFloat()
        pointStrokeColor = ta.getColor(R.styleable.LineChartView_pointStrokeColor, lineChartColor)
        pointStrokeWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_pointStrokeWidth, (lineChartWidth / 2).toInt()).toFloat()
        pointSelectedStrokeColor = ta.getColor(R.styleable.LineChartView_pointSelectedStrokeColor, lineChartColor)
        pointSelectedStrokeWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_pointSelectedStrokeWidth,
            (pointStrokeWidth * 2).toInt()).toFloat()
        pointSelectedColor = ta.getColor(R.styleable.LineChartView_pointSelectedColor, pointColor)
        pointSelectedRadius = ta.getDimensionPixelSize(R.styleable.LineChartView_pointSelectedRadius,
            (pointRadius + pointStrokeWidth / 2).toInt()).toFloat()
        pointSelectedOutStrokeColor = ta.getColor(R.styleable.LineChartView_pointSelectedOutStrokeColor, pointSelectedOutStrokeColor)
        pointSelectedOutStrokeWidth = ta.getDimensionPixelSize(R.styleable.LineChartView_pointSelectedOutStrokeWidth, pointSelectedRadius.toInt()).toFloat()
        pointStartX = ta.getDimensionPixelSize(R.styleable.LineChartView_pointStartX, pointStartX)
        pointEndX = ta.getDimensionPixelSize(R.styleable.LineChartView_pointEndX, pointEndX)

        showPointFloatBox = ta.getBoolean(R.styleable.LineChartView_showPointFloatBox, showPointFloatBox)
        floatBoxColor = ta.getColor(R.styleable.LineChartView_floatBoxColor, floatBoxColor)
        floatBoxTextColor = ta.getColor(R.styleable.LineChartView_floatBoxTextColor, floatBoxTextColor)
        floatBoxTextSize = ta.getDimensionPixelSize(R.styleable.LineChartView_floatBoxTextSize, floatBoxTextSize.toInt()).toFloat()
        floatBoxPadding = ta.getDimensionPixelSize(R.styleable.LineChartView_floatBoxPadding, floatBoxPadding.toInt()).toFloat()
        ta.recycle()
    }

    private fun initPaint(){
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.FILL
        linePaint.strokeCap = Paint.Cap.ROUND
        lineChartPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        lineChartPaint.color = lineChartColor
        lineChartPaint.style = Paint.Style.STROKE
        lineChartPaint.strokeWidth = lineChartWidth
        lineChartPaint.strokeCap = Paint.Cap.ROUND
        lineChartPaint.strokeJoin = Paint.Join.ROUND
        limitPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        limitPaint.color = limitLineColor
        limitPaint.style = Paint.Style.STROKE
        limitPaint.strokeWidth = limitLineWidth
        limitPaint.pathEffect = DashPathEffect(floatArrayOf(limitLineSpace, limitLineLength), 0f)
        pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pointPaint.strokeCap = Paint.Cap.ROUND
        pointPaint.textAlign = Paint.Align.CENTER
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun initData() {
        if(showPointFloatBox) {
            selectedIndex = pointList.size - 1
        }
        if(pointSpace > 0){
            drawWidth = pointSpace * xAxisList.size - pointEndX
            minSlideX = viewWidth - pointSpace * xAxisList.size - pointStartX
            //如果大于0，说明绘制没有超过当前View宽度，不需要滑动
            if(minSlideX > 0){
                minSlideX = maxSlideX
            }
        } else {
            drawWidth = (viewWidth - lineChartPaddingStart).toFloat() - pointStartX - pointEndX
            minSlideX = maxSlideX
        }
        Timber.d("slideX=$slideX, minSlideX=$minSlideX, maxSlideX=$maxSlideX")
    }

    private var viewWidth = 0
    private var viewHeight = 0
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) {
            super.onLayout(changed, left, top, right, bottom)
            return
        }
        viewWidth = width
        viewHeight = height
        originX = lineChartPaddingStart + yAxisWidth
        originY = viewHeight - lineChartPaddingBottom * 1f
        drawHeight = originY - lineChartPaddingTop
        slideX = originX
        maxSlideX = slideX
        saveRect.set(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        clearRect.set(0f, 0f, originX, viewHeight.toFloat())
        super.onLayout(changed, left, top, right, bottom)
    }

    private val porterDuffMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    override fun onDraw(canvas: Canvas) {
        if (pointList.isNotEmpty()) {
            canvas.drawColor(lineChartBgColor)
            drawXAxis(canvas)
            drawYAxis(canvas)
            drawLimitLine(canvas)
            //重新开一个图层
            val layerId = canvas.saveLayer(saveRect, null)
            if(showLineChartAnim){
                drawAnimBrokenLine(canvas)
            } else {
                drawLineChart(canvas)
            }
            if(showLineChartPoint){
                drawLineChartPoint(canvas)
            }
            // 将折线超出x轴坐标的部分截取掉
            linePaint.xfermode = porterDuffMode
            canvas.drawRect(clearRect, linePaint)
            linePaint.xfermode = null
            //保存图层
            canvas.restoreToCount(layerId)
        }
    }

    private fun drawXAxis(canvas: Canvas) {
        if(showXAxis){
            linePaint.color = xAxisColor
            linePaint.strokeWidth = xAxisWidth
            val stopX = if (showAxisArrow) {
                viewWidth.toFloat() - axisArrowWidth / 2
            } else {
                viewWidth.toFloat()
            }
            canvas.drawLine(originX, originY, stopX, originY, linePaint)
            Timber.d("stopX = $stopX")
            if (showAxisArrow) {
                linePaint.color = axisArrowColor
                if(xAxisArrowPath == null){
                    xAxisArrowPath = Path()
                    xAxisArrowPath!!.moveTo(viewWidth.toFloat() - axisArrowWidth, (originY - axisArrowHeight))
                    xAxisArrowPath!!.lineTo(viewWidth.toFloat() - xAxisWidth, originY * 1f)
                    xAxisArrowPath!!.lineTo(viewWidth.toFloat() - axisArrowWidth, (originY + axisArrowHeight))
                }
                canvas.drawPath(xAxisArrowPath!!, linePaint)
            }
        }
        if(!showXScaleLine && !showXText){
            return
        }
        xAxisList.forEach { axisInfo ->
            val x = getDrawX(axisInfo.value)
            if (x < originX) {
                return@forEach
            }
            if (showXScaleLine) {
                linePaint.color = xScaleLineColor
                linePaint.strokeWidth = xScaleLineWidth
                canvas.drawLine(x, originY + xAxisWidth / 2f, x, originY + xAxisWidth / 2f + xScaleLineLength, linePaint)
            }

            if (showXText) {
                textPaint.color = xTextColor
                textPaint.textSize = xTextSize
                canvas.drawText(axisInfo.showText, 0, axisInfo.showText.length, x,
                    originY + (lineChartPaddingBottom + xTextHeight) / 2f, textPaint)
            }
        }
    }

    private fun drawYAxis(canvas: Canvas) {
        if(showYAxis){
            linePaint.color = yAxisColor
            linePaint.strokeWidth = yAxisWidth
            val stopY = if (showAxisArrow) {
                axisArrowHeight
            } else {
                0f
            }
            canvas.drawLine(originX, originY - xAxisWidth, originX, stopY, linePaint)
            if (showAxisArrow) {
                linePaint.color = axisArrowColor
                if(yAxisArrowPath == null){
                    yAxisArrowPath = Path()
                }
                yAxisArrowPath!!.moveTo((originX - axisArrowHeight), axisArrowWidth)
                yAxisArrowPath!!.lineTo((originX), yAxisWidth)
                yAxisArrowPath!!.lineTo((originX + axisArrowHeight), axisArrowWidth)
                canvas.drawPath(yAxisArrowPath!!, linePaint)
            }
        }
        if(!showYScaleLine && !showYText){
            return
        }
        textPaint.textAlign = when (yTextAlign) {
            TextAlign.LEFT -> Paint.Align.LEFT
            TextAlign.CENTER -> Paint.Align.CENTER
            TextAlign.RIGHT -> Paint.Align.RIGHT
        }
        yAxisList.forEachIndexed { _, axisInfo ->
            if (showYScaleLine) {
                linePaint.color = yScaleLineColor
                linePaint.strokeWidth = yScaleLineWidth
                val y = getDrawY(axisInfo.value)
                canvas.drawLine(originX, y, originX - yScaleLineLength, y, linePaint)
            }

            if (showYText) {
                textPaint.color = yTextColor
                textPaint.textSize = yTextSize
                val textX = when (yTextAlign) {
                    TextAlign.LEFT -> yScaleLineLength
                    TextAlign.CENTER -> (originX - yScaleLineLength) / 2
                    TextAlign.RIGHT -> originX - yScaleLineLength * 2
                }
                canvas.drawText(axisInfo.showText, textX,
                    getDrawY(axisInfo.value) + yTextHeight / 2 - yAxisWidth, textPaint)
            }
        }
    }

    fun setLimitArray(limitArray: List<Int>) {
        this.limitArray = limitArray
    }

    private fun drawLimitLine(canvas: Canvas) {
        if(limitArray != null){
            if(limitLinePath == null){
                limitLinePath = Path()
            } else {
                limitLinePath!!.reset()
            }
            limitArray!!.forEach {
                val y = getDrawY(it.toFloat())
                limitLinePath!!.moveTo(originX, y)
                limitLinePath!!.lineTo(viewWidth.toFloat(), y)
            }
            canvas.drawPath(limitLinePath!!, limitPaint)
        }
    }

    /**
     * 绘制折线点
     *
     * @param canvas
     */
    private fun drawLineChartPoint(canvas: Canvas) {
        var x: Float
        var y: Float
        //绘制普通的折线点
        pointList.forEach { point ->
            x = getDrawX(point.x)
            y = getDrawY(point.y)
            pointPaint.style = Paint.Style.FILL
            pointPaint.color = pointColor
            canvas.drawCircle(x, y, pointRadius, pointPaint)
            if(pointStrokeWidth > 0){
                pointPaint.style = Paint.Style.STROKE
                pointPaint.strokeWidth = pointStrokeWidth
                pointPaint.color = pointStrokeColor
                canvas.drawCircle(x, y, pointRadius, pointPaint)
            }
        }
        //绘制选中的折线点
        if (showPointFloatBox && selectedIndex >= 0) {
            val selectedPoint = pointList[selectedIndex]
            val selectedX = getDrawX(selectedPoint.x)
            val selectedY = getDrawY(selectedPoint.y)
            pointPaint.style = Paint.Style.FILL
            pointPaint.color = pointSelectedOutStrokeColor
            pointPaint.strokeWidth = pointSelectedOutStrokeWidth
            canvas.drawCircle(selectedX, selectedY, pointSelectedRadius + pointSelectedOutStrokeWidth, pointPaint)
            pointPaint.color = pointSelectedColor
            canvas.drawCircle(selectedX, selectedY, pointSelectedRadius, pointPaint)
            if(pointSelectedStrokeWidth > 0){
                pointPaint.style = Paint.Style.STROKE
                pointPaint.strokeWidth = pointSelectedStrokeWidth
                pointPaint.color = pointSelectedStrokeColor
                canvas.drawCircle(selectedX, selectedY, pointSelectedRadius, pointPaint)
            }
            drawFloatTextBox(canvas, selectedX, selectedY - floatBoxPadding * 2, selectedPoint.y)
        }
    }

    /**
     * 绘制显示Y值的浮动框
     *
     */
    private fun drawFloatTextBox(canvas: Canvas, x: Float, y: Float, value: Float) {
        pointPaint.color = floatBoxColor
        pointPaint.style = Paint.Style.FILL
        val text = value.scale(2).toString()
        val rect = Rect()
        textPaint.textSize = floatBoxTextSize
        textPaint.color = floatBoxTextColor
        textPaint.getTextBounds(text, 0, text.length, rect)

        val boxWidth = rect.width() / 2f + floatBoxPadding
        val boxHeight = rect.height() + floatBoxPadding * 2
        val cornerPathEffect = CornerPathEffect(2.5f.dp)
        pointPaint.pathEffect = cornerPathEffect
        if(textBoxPath == null){
            textBoxPath = Path()
        } else {
            textBoxPath!!.reset()
        }
        textBoxPath!!.moveTo(x, y)
        textBoxPath!!.lineTo(x - floatBoxPadding, y - floatBoxPadding)
        textBoxPath!!.lineTo(x - boxWidth, y - floatBoxPadding)
        textBoxPath!!.lineTo(x - boxWidth, y - boxHeight - floatBoxPadding)
        textBoxPath!!.lineTo(x + boxWidth, y - boxHeight - floatBoxPadding)
        textBoxPath!!.lineTo(x + boxWidth, y - floatBoxPadding)
        textBoxPath!!.lineTo(x + floatBoxPadding, y - floatBoxPadding)
        textBoxPath!!.lineTo(x, y)
        canvas.drawPath(textBoxPath!!, pointPaint)
        canvas.drawText(text, x, y - boxHeight / 2, textPaint)
    }

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f
    private fun drawLineChart(canvas: Canvas) {
        lineChartPath.reset()
        pointList.forEachIndexed { index, point ->
            endX = getDrawX(point.x)
            endY = getDrawY(point.y)
            if(drawCurve){
                //绘制曲线（三阶贝塞尔曲线）
                if (index == 0) {
                    lineChartPath.moveTo(endX, endY)
                } else {
                    val referX = (startX + endX) / 2
                    lineChartPath.cubicTo(referX, startY, referX, endY, endX, endY)
                }
                startX = endX
                startY = endY
            } else {
                //绘制折线
                if (index == 0) {
                    lineChartPath.moveTo(endX, endY)
                } else {
                    lineChartPath.lineTo(endX, endY)
                }
            }
        }
        canvas.drawPath(lineChartPath, lineChartPaint)
    }

    private var currentAnimValue: Float = -1f
    private var animPathMeasure: PathMeasure? = null
    private fun drawAnimBrokenLine(canvas: Canvas) {
        if(currentAnimValue == -1f){
            lineChartPath.reset()
            pointList.forEachIndexed { index, point ->
                endX = getDrawX(point.x)
                endY = getDrawY(point.y)
                if(drawCurve){
                    //绘制曲线（三阶贝塞尔曲线）
                    if (index == 0) {
                        lineChartPath.moveTo(endX, endY)
                    } else {
                        val referX = (startX + endX) / 2
                        lineChartPath.cubicTo(referX, startY, referX, endY, endX, endY)
                    }
                    startX = endX
                    startY = endY
                } else {
                    //绘制折线
                    if(index == 0){
                        lineChartPath.moveTo(endX, endY)
                    } else {
                        lineChartPath.lineTo(endX, endY)
                    }
                }
            }
            animPathMeasure = PathMeasure(lineChartPath, false)
            val animator = ValueAnimator.ofFloat(1f, 0f).setDuration(drawWidth.toLong())
            animator.addUpdateListener {
                currentAnimValue = it.animatedValue as Float
                invalidate()
            }
            animator.doOnEnd {
                showLineChartAnim = false
                lineChartPaint.pathEffect = null
                animPathMeasure = null
            }
            animator.start()
        } else {
            val effect = DashPathEffect(floatArrayOf(animPathMeasure!!.length,
                animPathMeasure!!.length), animPathMeasure!!.length * currentAnimValue)
            lineChartPaint.pathEffect = effect
            canvas.drawPath(lineChartPath, lineChartPaint)
        }
    }

    private fun getDrawX(pointX: Float): Float {
        return slideX + drawWidth * ((pointX - xMin) / (xMax - xMin)) + pointStartX
    }

    private fun getDrawY(pointY: Float): Float {
        //处理负值的情况，但是point的y值必须在最大值和最小值之间
        return drawHeight - drawHeight * ((pointY - yMin) / (yMax - yMin)) + lineChartPaddingTop
    }

    enum class SlideSate{
        LEFT_AND_RIGHT, UP_AND_DOWN, NONE
    }

    private var slideSate = SlideSate.NONE
    private var velocityTracker: VelocityTracker? = null
    private val scroller = OverScroller(context)
    private var startTouchX: Float = 0f
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var distanceX = 0f
    private var distanceY = 0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //判断动画播放中和禁止滑动
        if(currentAnimValue > 0f){
            return false
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker!!.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if(slideSate == SlideSate.NONE){
                    distanceX = abs(event.x - downX)
                    distanceY = abs(event.y - downY)
                    Timber.d("downX=$downX,downY=$downY, distanceX=$distanceX,distanceY=$distanceY")
                    if(distanceX > 20 && distanceX > distanceY){
                        startTouchX = event.x
                        slideSate = SlideSate.LEFT_AND_RIGHT
                    }
                    if(distanceY > 20 && distanceY > distanceX){
                        slideSate = SlideSate.UP_AND_DOWN
                    }
                } else if(slideSate == SlideSate.UP_AND_DOWN){
                    parent.requestDisallowInterceptTouchEvent(false)
                } else if(slideSate == SlideSate.LEFT_AND_RIGHT){
                    parent.requestDisallowInterceptTouchEvent(true)
                    val moveX = event.x - startTouchX
                    startTouchX = event.x
                    slideX = when {
                        slideX + moveX < minSlideX -> minSlideX
                        slideX + moveX > maxSlideX -> maxSlideX
                        else -> slideX + moveX
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                slideSate = SlideSate.NONE
                if(showPointFloatBox){
                    clickAction(event)
                }
                velocityTracker!!.computeCurrentVelocity(500)
                val velocityY = velocityTracker!!.yVelocity.toInt()
                val velocityX = velocityTracker!!.xVelocity.toInt()
                val y = event.y.toInt()
                scroller.fling(slideX.toInt(), y, velocityX, velocityY,
                    minSlideX.toInt(), maxSlideX.toInt(), y, y)
                ViewCompat.postOnAnimation(this, flingRunnable)
                velocityTracker!!.recycle()
                velocityTracker = null
            }
        }
        return true
    }

    private val flingRunnable = Runnable{
        if(slideSate == SlideSate.NONE && scroller.computeScrollOffset()){
            slideX = scroller.currX.toFloat()
            invalidate()
            postOnAnimation()
        }
    }

    private fun postOnAnimation() {
        postOnAnimation(flingRunnable)
    }

    /**
     * 点击X轴坐标或者折线节点
     *
     */
    private val clickArea = 8f.dp
    private fun clickAction(event: MotionEvent) {
        val eventX = event.x
        val eventY = event.y
        for (index in pointList.indices) {
            val point = pointList[index]
            val x = getDrawX(point.x)
            val y = getDrawY(point.y)
            if (eventX >= x - clickArea && eventX <= x + clickArea &&
                eventY >= y - clickArea && eventY <= y + clickArea && selectedIndex != index
            ) {//每个节点周围8dp都是可点击区域
                selectedIndex = index
                invalidate()
                return
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        xAxisArrowPath?.reset()
        xAxisArrowPath = null
        yAxisArrowPath?.reset()
        yAxisArrowPath = null
        limitLinePath?.reset()
        limitLinePath = null
        textBoxPath?.reset()
        textBoxPath = null
        lineChartPath.reset()
    }

    /** 设置是否绘制曲线 */
    fun setDrawCurve(drawCurve: Boolean){
        this.drawCurve = drawCurve
        invalidate()
    }

    /** 设置是否显示折线点 */
    fun setLineChartPoint(show: Boolean){
        showLineChartPoint = show
        invalidate()
    }

    /** 设置显示折线动画 */
    fun showLineChartAnim(){
        if(!showLineChartAnim){
            currentAnimValue = -1f
            showLineChartAnim = true
            invalidate()
        }
    }

    /**
     * 设置折线数据
     *
     * @param pointList 点的集合
     * @param xAxisList X轴数据集合
     * @param yAxisList Y轴数据集合
     * @param pointSpace 点的间距
     *
     * */
    @JvmOverloads
    fun setData(pointList: List<PointInfo>, xAxisList: List<AxisInfo>? = null, yAxisList: List<AxisInfo>, pointSpace: Float = 0f){
        this.pointList = pointList.sortedBy { it.x }
        this.xAxisList = xAxisList?.sortedBy { it.value } ?: this.pointList.map { AxisInfo(it.x)}
        this.yAxisList = yAxisList.sortedBy { it.value }
        this.pointSpace = pointSpace
        doOnPreDraw {
            initData()
            invalidate()
        }
    }

}