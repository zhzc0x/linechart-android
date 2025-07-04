package com.zhzc0x.chart.demo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.zhzc0x.chart.AmplitudeMode
import com.zhzc0x.chart.AxisInfo
import com.zhzc0x.chart.PointInfo
import com.zhzc0x.chart.ShowPointInfo
import com.zhzc0x.chart.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pointXInitValueList = listOf(0, 12, 24)
    private var pointXStartDp = 0
    private var pointXEndDp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lineChartView.setLimitArray(listOf(-50f, 0f, 50f, 100f))
        val pointList = ArrayList<PointInfo>()
        (1..20).forEach { i ->
            pointList.add(PointInfo(i.toFloat(), (-100..100).random().toFloat()))
        }
        val xAxisList = pointList.map { pointInfo ->
            AxisInfo(pointInfo.x, pointInfo.x.toInt().toString())
        }
        binding.lineChartView.setData(
            pointList, xAxisList = xAxisList, yAxisList = listOf(
                AxisInfo(-100f, "-100"),
                AxisInfo(-50f, "-50"),
                AxisInfo(0f, "0"),
                AxisInfo(50f, "50"),
                AxisInfo(100f, "100")
            ), pointSpace = 60f
        )
        binding.drawTypeSpinner.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            listOf("折线", "曲线")
        )
        binding.drawTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    binding.lineChartView.setDrawCurve(position == 1)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        binding.pointXStartSpinner.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            pointXInitValueList.map { "${it}dp" })
        binding.pointXStartSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    pointXStartDp = pointXInitValueList[position]
                    binding.lineChartView.setPointXInit(pointXStartDp, pointXEndDp)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.pointXEndSpinner.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            pointXInitValueList.map { "${it}dp" })
        binding.pointXEndSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    pointXEndDp = pointXInitValueList[position]
                    binding.lineChartView.setPointXInit(pointXStartDp, pointXEndDp)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        binding.cbShowChartPoint.setOnCheckedChangeListener { _, checked ->
            binding.lineChartView.setShowLineChartPoint(checked)
        }

        val showPointList = listOf(
            ShowPointInfo(
                pointList[2].x, pointList[2].y, 9f, Color.WHITE, 3.5f,
                Color.RED, pointList[2].y.toString(), 32f, Color.RED, 12f
            ),
            ShowPointInfo(
                pointList[6].x, pointList[6].y, 9f, Color.WHITE, 3.5f,
                Color.BLUE, pointList[6].y.toString(), 32f, Color.BLUE, 12f
            )
        )
        binding.cbShowPoints.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.lineChartView.setShowPoints(showPointList)
            } else {
                binding.lineChartView.setShowPoints(null)
            }
        }

        binding.btnAnim.setOnClickListener {
            binding.lineChartView.showLineChartAnim()
        }

        binding.drawTypeSpinner2.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            listOf("折线", "曲线")
        )
        binding.drawTypeSpinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    binding.liveLineChartView.setDrawCurve(position == 1)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        var xLimitCount = 0
        var yLimitCount = 2
        val yAmplitudeRangeList = listOf("100", "0.2", "0.4", "0.6", "0.8", "1", "2", "4", "自动-MAX_NEGATE", "自动-MAX_MIN")
        var curAmplitudeRange = 0f
        binding.yMaxSpinner.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            yAmplitudeRangeList
        )
        binding.yMaxSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                if (position == yAmplitudeRangeList.size - 1) {
                    binding.liveLineChartView.setAmplitudeMode(AmplitudeMode.MAX_MIN)
                } else if (position == yAmplitudeRangeList.size - 2) {
                    binding.liveLineChartView.setAmplitudeMode(AmplitudeMode.MAX_NEGATE)
                } else {
                    binding.liveLineChartView.setAmplitudeMode(AmplitudeMode.FIXED)
                    curAmplitudeRange = yAmplitudeRangeList[position].toFloat()
                    updateYAxisInfos(curAmplitudeRange, yLimitCount)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val pointSpaceList =
            listOf("1dp", "1.2dp", "1.4dp", "1.6dp", "1.8dp", "2dp", "3dp", "4dp", "5dp")
        binding.pointSpaceSpinner.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            pointSpaceList
        )
        binding.pointSpaceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val pointSpace = pointSpaceList[position].replace("dp", "").toFloat()
                    binding.liveLineChartView.setPointSpace(pointSpace)
                    binding.liveLineChartView.setAutoAmplitudePoints(0.5f)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.pointSpaceSpinner.setSelection(pointSpaceList.lastIndex)

        val xLimitLineCountList = listOf("0", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        binding.xLimitCountSpinner.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            xLimitLineCountList
        )
        binding.xLimitCountSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    xLimitCount = xLimitLineCountList[position].toInt()
                    binding.liveLineChartView.setLimitLineCount(xLimitCount, yLimitCount)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        val yLimitLineCountList = listOf("3", "4", "5", "6", "7")
        binding.yLimitCountSpinner.adapter = ArrayAdapter(
            this, R.layout.item_spinner_textview,
            yLimitLineCountList
        )
        binding.yLimitCountSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    yLimitCount = yLimitLineCountList[position].toInt()
                    binding.liveLineChartView.setLimitLineCount(xLimitCount, yLimitCount)
                    updateYAxisInfos(curAmplitudeRange, yLimitCount)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        lifecycleScope.launch {
            delay(1000)
            while (true) {
                delay(16)
                binding.liveLineChartView.addPoint((-900000..900000).random() / 10000f)
//                binding.liveLineChartView.addPoint(56250f)
            }
        }
    }

    private fun updateYAxisInfos(amplitudeRange: Float, yLimitCount: Int) {
        val yAxisList = (0 until yLimitCount).map { i ->
            AxisInfo((amplitudeRange - amplitudeRange * 2 / (yLimitCount - 1) * i))
        }
        binding.liveLineChartView.setData(yAxisList)
        binding.liveLineChartView.setAutoAmplitudeFactor(0.00001f)
    }
}
