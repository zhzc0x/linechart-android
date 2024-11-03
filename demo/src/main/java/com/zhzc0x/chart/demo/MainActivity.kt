package com.zhzc0x.chart.demo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
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
        val yAmplitudeRangeList = listOf("0.2", "0.4", "0.6", "0.8", "1", "2", "4", "自动")
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
                    binding.liveLineChartView.setAutoAmplitude(true)
                } else {
                    val amplitudeRange = yAmplitudeRangeList[position].toFloat()
                    binding.liveLineChartView.setData(
                        listOf(
                            AxisInfo(amplitudeRange),
                            AxisInfo(0f), AxisInfo(-amplitudeRange)
                        ), yAxisUnit = "mV"
                    )
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
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        lifecycleScope.launch {
            delay(1000)
            while (true) {
                delay(16)
                binding.liveLineChartView.addPoint((-90000..90000).random() / 10000f)
            }
        }
    }
}
