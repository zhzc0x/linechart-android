package com.github.zicheng.chart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.github.zicheng.chart.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pointList = ArrayList<PointInfo>()
        (1..20).forEach { i ->
            pointList.add(PointInfo(i.toFloat(), (-100..100).random().toFloat()))
        }
        binding.lineChartView.setLimitArray(listOf(-50f, 0f, 50f, 100f))
        binding.lineChartView.setData(pointList, yAxisList = listOf(
            AxisInfo(-100f),
            AxisInfo(-50f),
            AxisInfo(0f),
            AxisInfo(50f),
            AxisInfo(100f)), pointSpace = 60f)
        binding.drawTypeSpinner.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            listOf("折线", "曲线"))
        binding.drawTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                binding.lineChartView.setDrawCurve(position == 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.cbShowChartPoint.setOnCheckedChangeListener{ _, checked ->
            binding.lineChartView.setLineChartPoint(checked)
        }
        binding.btnAnim.setOnClickListener {
            binding.lineChartView.showLineChartAnim()
        }

        binding.drawTypeSpinner2.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            listOf("折线", "曲线"))
        binding.drawTypeSpinner2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                binding.liveLineChartView.setDrawCurve(position == 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val yMaxList = listOf("0.2", "0.4", "0.6", "0.8", "1", "2", "4", "自动")
        binding.yMaxSpinner.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            yMaxList)
        binding.yMaxSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                if(position == yMaxList.size - 1){
                    binding.liveLineChartView.setAutoZoomYMax(true)
                } else {
                    val yMax = yMaxList[position].toFloat()
                    binding.liveLineChartView.setData(listOf(AxisInfo(yMax),
                        AxisInfo(0f), AxisInfo(-yMax)), yAxisUnit = "mV")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val pointSpaceList = listOf("1dp", "1.2dp", "1.4dp", "1.6dp", "1.8dp", "2dp", "3dp", "4dp", "5dp")
        binding.pointSpaceSpinner.adapter = ArrayAdapter(this, R.layout.item_spinner_textview,
            pointSpaceList)
        binding.pointSpaceSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val pointSpace = pointSpaceList[position].replace("dp", "").toFloat()
                binding.liveLineChartView.setPointSpace(pointSpace)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        lifecycleScope.launch {
            delay(1000)
            while (true){
                delay(20)
                binding.liveLineChartView.addPoint((-9000..9000).random() / 10000f)
            }
        }

    }
}