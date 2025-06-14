package com.zhzc0x.chart

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.zhzc0x.chart.ext.dp
import com.zhzc0x.chart.ext.scale
import kotlin.math.abs
import kotlin.math.max

class LiveLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val tag = LiveLineChartView::class.java.simpleName
    var debug = false

    private var showYAxis = true
    private var yAxisColor = Color.GRAY
    private var yAxisWidth = 1f.dp
    private var showYText = true
    private var yTextColor = 0
    private var yTextSize = 10f.dp
    private var yTextAlign = TextAlign.RIGHT
    private var showYScaleLine = true
    private var yScaleLineColor = 0
    private var yScaleLineWidth = 1f.dp
    private var yScaleLineLength = 4f.dp

    private var limitLineColor = Color.GRAY
    private var limitLineWidth = 1f.dp
    private var limitLineLength = 2f.dp
    private var limitLineSpace = 2f.dp
    private var xLimitLineCount = 2
    private var yLimitLineCount = 2

    private var lineChartWidth = 1.5f.dp
    private var lineChartColor = Color.LTGRAY
    private var lineChartPaddingStart = 30.dp
    private var lineChartBgColor = Color.WHITE
    private var drawCurve = false // 绘制曲线
    private var pointSpace = 1.2f.dp // 折线点间距

    private lateinit var linePaint: Paint
    private lateinit var lineChartPaint: Paint
    private lateinit var limitPaint: Paint
    private lateinit var textPaint: TextPaint

    // 折线点对应的数据
    private val pointList = ArrayList<Float>()
    // x起始位置
    private var xOrigin = 0f
    // y起始位置
    private var yOrigin = 0f
    private var drawHeight = 0f
    private var yMin = 0f
    private var yMax = 1f
    private var yMinLimit = 0f // y轴最小值限制
    // y轴坐标对应的数据
    private val yAxisList = ArrayList<AxisInfo>()
    private var textConverter: (Float) -> String = { it.scale(1).toString() }
    private val lineChartPath = Path()
    private var limitLinePath: Path? = null
    private var yTextHeight = 0f
    private var autoAmplitude = false // 自动缩放Y轴幅值
    private var screenMaxPoints = 0 // 屏幕最大点数
    private var autoPointsThreshold = 0 // 自动缩放Y轴幅值点数阈值

    init {
        if (attrs != null) {
            initCustomAttrs(context, attrs)
        }
        initPaint()
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LiveLineChartView)
        showYAxis = ta.getBoolean(R.styleable.LiveLineChartView_showYAxis, showYAxis)
        yAxisColor = ta.getColor(R.styleable.LiveLineChartView_yAxisColor, yAxisColor)
        yAxisWidth = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_yAxisWidth, yAxisWidth.toInt()).toFloat()
        showYText = ta.getBoolean(R.styleable.LiveLineChartView_showYText, showYText)
        yTextColor = ta.getColor(R.styleable.LiveLineChartView_yTextColor, yAxisColor)
        yTextSize = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_yTextSize, yTextSize.toInt()).toFloat()
        val ordinal = ta.getInt(R.styleable.LiveLineChartView_yTextAlign, yTextAlign.ordinal)
        yTextAlign = TextAlign.values()[ordinal]
        showYScaleLine = ta.getBoolean(R.styleable.LiveLineChartView_showYScaleLine, showYScaleLine)
        yScaleLineColor = ta.getColor(R.styleable.LiveLineChartView_yScaleLineColor, yAxisColor)
        yScaleLineWidth = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_yScaleLineWidth, yScaleLineWidth.toInt()).toFloat()
        yScaleLineLength = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_yScaleLineLength, yScaleLineLength.toInt()).toFloat()

        limitLineColor = ta.getColor(R.styleable.LiveLineChartView_limitLineColor, limitLineColor)
        limitLineWidth = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_limitLineWidth, limitLineWidth.toInt()).toFloat()
        limitLineLength = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_limitLineLength, limitLineLength.toInt()).toFloat()
        limitLineSpace = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_limitLineSpace, limitLineSpace.toInt()).toFloat()
        val xLimitLineCount = ta.getInt(R.styleable.LiveLineChartView_xLimitLineCount, xLimitLineCount)
        val yLimitLineCount = ta.getInt(R.styleable.LiveLineChartView_yLimitLineCount, yLimitLineCount)
        setLimitLineCount(xLimitLineCount, yLimitLineCount)

        lineChartColor = ta.getColor(R.styleable.LiveLineChartView_lineChartColor, lineChartColor)
        lineChartWidth = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_lineChartWidth, lineChartWidth.toInt()).toFloat()
        lineChartPaddingStart = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_lineChartPaddingStart, lineChartPaddingStart)
        lineChartBgColor = ta.getColor(R.styleable.LiveLineChartView_lineChartBgColor, lineChartBgColor)
        drawCurve = ta.getBoolean(R.styleable.LiveLineChartView_drawCurve, drawCurve)
        pointSpace = ta.getDimensionPixelSize(R.styleable.LiveLineChartView_pointSpace, pointSpace.toInt()).toFloat()
        if (debug) {
            Log.d(tag, "pointSpace=$pointSpace")
        }
        ta.recycle()
    }

    private fun initPaint() {
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
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
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun initData() {
        xOrigin = lineChartPaddingStart + yAxisWidth
        yOrigin = viewHeight.toFloat()
        updateScreenMaxPoints()
        textPaint.textSize = yTextSize
        val text = "TEXT"
        val textRect = Rect()
        textPaint.getTextBounds(text, 0, text.length, textRect)
        yTextHeight = textRect.height() * 1.5f
        drawHeight = yOrigin - yTextHeight
    }

    private fun updateScreenMaxPoints() {
        screenMaxPoints = ((viewWidth - lineChartPaddingStart) / pointSpace).toInt()
        if (debug) {
            Log.d(tag, "drawScreenWidth=${viewWidth - lineChartPaddingStart}, " +
                    "pointSpace=${pointSpace}, screenMaxPoints=$screenMaxPoints")
        }
        // 边界case：防止页面未测量完成时出现负数
        if (screenMaxPoints < 0) {
            screenMaxPoints = 0
        }
        autoPointsThreshold = screenMaxPoints
    }

    private var viewWidth = 0
    private var viewHeight = 0
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) {
            super.onLayout(false, left, top, right, bottom)
            return
        }
        viewWidth = width
        viewHeight = height
        initData()
        super.onLayout(true, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(lineChartBgColor)
        drawYAxis(canvas)
        drawLimitLine(canvas)
        drawLineChart(canvas)
    }

    private fun drawYAxis(canvas: Canvas) {
        if (showYAxis) {
            linePaint.color = yAxisColor
            linePaint.strokeWidth = yAxisWidth
            canvas.drawLine(xOrigin, yOrigin, xOrigin, 0f, linePaint)
        }
        if (!showYScaleLine && !showYText) {
            return
        }
        yAxisList.forEach { axisInfo ->
            if (showYScaleLine) {
                linePaint.color = yScaleLineColor
                linePaint.strokeWidth = yScaleLineWidth
                val y = getDrawY(axisInfo.value)
                canvas.drawLine(xOrigin, y, xOrigin - yScaleLineLength, y, linePaint)
            }

            if (showYText) {
                textPaint.color = yTextColor
                textPaint.textSize = yTextSize
                textPaint.textAlign = when (yTextAlign) {
                    TextAlign.LEFT -> Paint.Align.LEFT
                    TextAlign.CENTER -> Paint.Align.CENTER
                    TextAlign.RIGHT -> Paint.Align.RIGHT
                }
                val textX = when (yTextAlign) {
                    TextAlign.LEFT -> yScaleLineLength
                    TextAlign.CENTER -> (xOrigin - yScaleLineLength) / 2
                    TextAlign.RIGHT -> xOrigin - yScaleLineLength * 2
                }
                canvas.drawText(axisInfo.showText, textX,
                    getDrawY(axisInfo.value) + yTextHeight / 3 - yAxisWidth, textPaint)
            }
        }
    }

    /**
     * 绘制限定线
     *
     * */
    private fun drawLimitLine(canvas: Canvas) {
        // draw x limit
        if (xLimitLineCount > 0) {
            resetLimitPath()
            val limitSpace = (viewWidth - xOrigin) / xLimitLineCount
            (1 .. xLimitLineCount).forEach {
                val x = it * limitSpace + xOrigin
                limitLinePath!!.moveTo(x, yOrigin)
                limitLinePath!!.lineTo(x, 0f)
            }
            canvas.drawPath(limitLinePath!!, limitPaint)
        }
        // draw y limit
        if (yLimitLineCount > 0) {
            resetLimitPath()
            val limitSpace = drawHeight / (yLimitLineCount - 1)
            (0 until yLimitLineCount).forEach {
                val y = it * limitSpace + yTextHeight / 2
                limitLinePath!!.moveTo(xOrigin, y)
                limitLinePath!!.lineTo(viewWidth.toFloat(), y)
            }
            canvas.drawPath(limitLinePath!!, limitPaint)
        }
    }

    private fun resetLimitPath() {
        if (limitLinePath == null) {
            limitLinePath = Path()
        } else {
            limitLinePath!!.reset()
        }
    }

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f
    private fun drawLineChart(canvas: Canvas) {
        lineChartPath.reset()
        pointList.forEachIndexed { index, y ->
            endX = getDrawX(index)
            endY = getDrawY(y)
            if (drawCurve) {
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

    private fun getDrawX(index: Int): Float {
        return xOrigin + pointSpace * index
    }

    private fun getDrawY(point: Float): Float {
        val temp = when {
            point > yMax -> {
                yMax
            }
            point < yMin -> {
                yMin
            }
            else -> {
                point
            }
        }
        //处理负值的情况，但是point的y值必须在最大值和最小值之间
        return drawHeight - drawHeight * ((temp - yMin) / (yMax - yMin)) + yTextHeight / 2
    }

    fun addPoint(point: Float) {
        if (pointList.size > screenMaxPoints) {
            pointList.removeAt(0)
        }
        pointList.add(point)
        if (autoAmplitude) {
            calculateAmplitude(point)
        }
        invalidate()
    }

    private var curMaxPoint = 0f
    private var preMaxPoint = 0f
    private var autoPoints = 0
    private fun calculateAmplitude(point: Float) {
        curMaxPoint = max(curMaxPoint, abs(point))
        // 控制每绘制固定的点数后计算一次幅值
        if (autoPoints >= autoPointsThreshold) {
            autoPoints = 0
            if (curMaxPoint == 0f) {
                return
            }
            if (debug) {
                Log.d(tag, "curMaxPoint=$curMaxPoint, preMaxPoint=$preMaxPoint")
            }
            if (curMaxPoint > preMaxPoint || curMaxPoint < preMaxPoint * 0.8f) {
                curMaxPoint *= 1.2f  // 增大20%
                if (curMaxPoint < yMinLimit) {
                    curMaxPoint = yMinLimit
                }
                updateYAxisList()
                updateAmplitude()
                preMaxPoint = curMaxPoint
            }
            curMaxPoint = 0f
        } else {
            autoPoints++
        }
    }

    private fun updateYAxisList() {
        val valueSpace = curMaxPoint * 2 / (yAxisList.size - 1)
        (0 until yAxisList.size).forEach { i ->
            val value = curMaxPoint - valueSpace * i
            yAxisList[i] = AxisInfo(value, textConverter(value))
        }
    }

    /**
     * 设置自动缩放Y轴幅值间隔，默认绘制一屏幕点的时间，根据addPoint()的频率计算
     *  @param timeMultiple: 绘制一屏幕点的时间倍数
     *  @see screenMaxPoints
     * */
    fun setAutoAmplitudeInterval(timeMultiple: Float) {
        autoPointsThreshold = (screenMaxPoints * timeMultiple).toInt()
    }

    fun reset() {
        pointList.clear()
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pointList.clear()
        limitLinePath?.reset()
        limitLinePath = null
        lineChartPath.reset()
    }

    /**
     * 设置是否绘制曲线
     * @see R.attr.drawCurve
     * */
    fun setDrawCurve(drawCurve: Boolean) {
        this.drawCurve = drawCurve
    }

    /**
     * 设置折线点间距，距离越大，折线移动速度越快，反之越小，单位：px
     * @see R.attr.pointSpace
     * */
    fun setPointSpace(pointSpace: Float) {
        this.pointSpace = pointSpace
        updateScreenMaxPoints()
        while (pointList.size > screenMaxPoints) {
            pointList.removeAt(0)
        }
    }

    /**
     * 设置是否自动缩放Y轴幅值
     * @param autoAmplitude Boolean
     * @param yMinLimit y轴最小值限制, autoAmplitude=true时生效
     * */
    fun setAutoAmplitude(autoAmplitude: Boolean, yMinLimit: Float) {
        this.autoAmplitude = autoAmplitude
        this.yMinLimit = yMinLimit
        autoPoints = pointList.size
        if (autoAmplitude) {
            preMaxPoint = 0f
        }
    }

    /** 设置x轴y轴限定线条数 */
    fun setLimitLineCount(xLimitLineCount: Int, yLimitLineCount: Int) {
        this.xLimitLineCount = xLimitLineCount
        this.yLimitLineCount = yLimitLineCount
        if (xLimitLineCount < 2) {
            if (yAxisList.size < 2) {
                throw IllegalArgumentException("xLimitLineCount must be greater than 1 !")
            }
        }
        invalidate()
    }

    /**
     * 设置折线数据
     *
     * @param yAxisList Y轴数据集合
     * @param autoAmplitude 是否自动缩放Y轴幅值
     * @param yMinLimit y轴最小值限制, autoAmplitude=true时生效
     * @param textConverter 文本转换
     *
     * */
    @JvmOverloads
    fun setData(
        yAxisList: List<AxisInfo>,
        autoAmplitude: Boolean = false,
        yMinLimit: Float = 0.1f,
        textConverter: (Float) -> String = this.textConverter
    ) {
        if (yAxisList.size < 2) {
            throw IllegalArgumentException("yAxisList.size must be greater than 1 !")
        }
        this.textConverter = textConverter
        this.yAxisList.clear()
        this.yAxisList.addAll(yAxisList.map { AxisInfo(it.value, this.textConverter(it.value)) })
        updateAmplitude()
        setAutoAmplitude(autoAmplitude, yMinLimit)
        if (yMax <= yMin) {
            throw IllegalArgumentException("yMax must be greater than yMin! yMax = the first element of yAxisList, yMin = the last element of yAxisList !")
        }
    }

    private fun updateAmplitude() {
        yMax = this.yAxisList.first().value
        yMin = this.yAxisList.last().value
        if (debug) {
            Log.d(tag, "updateAmplitude：yMax=$yMax, yMin=$yMin")
        }
    }
}
