# LineChartView-Android
静态波形、动态实时波形绘制

# Demo效果图

<img src="https://github.com/zicheng2019/linechart-android/blob/master/demo.gif" style="zoom:100%;" />

# 使用

- 添加gradle依赖（version=[![](https://jitpack.io/v/zicheng2019/linechart-android.svg)](https://jitpack.io/#zicheng2019/linechart-android)
）

```groovy
//Add it in your root build.gradle at the end of repositories:
 allprojects {
repositories {
   ...
      maven { url 'https://jitpack.io' }
   }
 }
 
 //Add it in your app build.gradle
 dependencies {
     implementation 'com.github.zicheng2019:linechart-android:$version'
 } 
```

- 布局文件中声明（更多属性说明详见 #自定义属性说明）

```xml
//静态波形LineChartView
<com.github.zicheng.chart.LineChartView
    android:id="@+id/lineChartView"
    android:layout_width="match_parent"
    android:layout_height="120dp"
    android:layout_gravity="start"
    android:layout_marginTop="12dp"
    app:axisArrowHeight="4dp"
    app:axisArrowWidth="7dp"
    app:lineChartBgColor="#f1f1f1"
    app:drawCurve="false"
    app:limitLineWidth="1dp"
    app:lineChartColor="#16422C"
    app:lineChartPaddingBottom="12dp"
    app:lineChartPaddingStart="42dp"
    app:lineChartWidth="2dp"
    app:showAxisArrow="true"
    app:showLineChartAnim="true"
    app:showLineChartPoint="false"
    app:showPointFloatBox="true"
    app:showXAxis="true"
    app:showXScaleLine="true"
    app:showXText="false"
    app:showYAxis="true"
    app:showYScaleLine="true"
    app:showYText="true"
    app:yTextColor="#16422C"
    app:yTextAlign="center"
    app:yTextSize="10sp"
    app:pointStartX="24dp"
    app:pointEndX="24dp"/>

//动态实时波形LiveLineChartView
<com.github.zicheng.chart.LiveLineChartView
        android:id="@+id/liveLineChartView"
        android:layout_width="match_parent"
        android:layout_height="120dp"
        android:layout_marginTop="12dp"
        app:lineChartBgColor="#f1f1f1"
        app:lineChartPaddingStart="50dp"
        app:pointSpace="2dp"
        app:limitLineCount="3"/>

```

- Api说明

```kotlin
class LineChartView {
    ......
    
    /** 设置限制线 */
    fun setLimitArray(limitArray: List<Float>)
    
    /** 设置是否绘制曲线 */
    fun setDrawCurve(drawCurve: Boolean)

    /** 设置是否显示折线点 */
    fun setLineChartPoint(show: Boolean)

    /** 设置显示折线动画 */
    fun showLineChartAnim()

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
    fun setData(pointList: List<PointInfo>, xAxisList: List<AxisInfo>? = null, yAxisList: List<AxisInfo>, 		                   pointSpace: Float = 0f)
    
    ......
}

class LiveLineChartView {
    ......
    
    /** 往当前屏幕添加折线点 */
    fun addPoint(point: Float)

    /**
     * 设置自动缩放Y轴最大最小值间隔，默认绘制一屏幕点的时间，根据addPoint()的频率计算
     *  @param multipleTime: 绘制一屏幕点的时间倍数
     *
     * */
    fun setAutoZoomInterval(multipleTime: Float)
	
    /** 清空当前屏幕所有的折线点 */
    fun reset()
    
    /** 设置是否绘制曲线 */
    fun setDrawCurve(drawCurve: Boolean)

    /**
     * 设置折线点间距，距离越大，折线移动速度越快，反之越小，单位：dp
     * */
    fun setPointSpace(pointSpace: Float)

    /** 设置自动缩放Y轴最大值 */
    fun setAutoZoomYMax(autoZoomYMax: Boolean)

    /**
     * 设置折线数据
     *
     * @param yAxisList Y轴数据集合
     * @param autoZoomYMax 自动缩放Y轴最大值
     *
     * */
    @JvmOverloads
    fun setData(yAxisList: List<AxisInfo>, autoZoomYMax: Boolean = false, yAxisUnit: String = "")
    
    ......
}
```

# 自定义属性说明

```xml
<declare-styleable name="LineChartView">
        <!-- 是否显示X轴 -->
        <attr name="showXAxis" format="boolean" />
        <!-- X轴颜色 -->
        <attr name="xAxisColor" format="reference|color" />
        <!-- X轴线宽 -->
        <attr name="xAxisWidth" format="dimension" />
        <!-- 是否显示X轴文字 -->
        <attr name="showXText" format="boolean" />
        <!-- X轴文字颜色 -->
        <attr name="xTextColor" format="reference|color" />
        <!-- X轴文字尺寸 -->
        <attr name="xTextSize" format="dimension" />
        <!-- 是否显示X轴刻度线 -->
        <attr name="showXScaleLine" format="boolean" />
        <!-- X轴刻度线颜色 -->
        <attr name="xScaleLineColor" format="reference|color" />
        <!-- X轴刻度线线宽 -->
        <attr name="xScaleLineWidth" format="dimension" />
        <!-- X轴刻度线长度 -->
        <attr name="xScaleLineLength" format="dimension" />

        <!-- 是否显示Y轴 -->
        <attr name="showYAxis" />
        <!-- Y轴颜色 -->
        <attr name="yAxisColor" />
        <!-- Y轴线宽 -->
        <attr name="yAxisWidth" />
        <!-- 是否显示Y轴文字 -->
        <attr name="showYText" />
        <!-- Y轴文字颜色 -->
        <attr name="yTextColor" />
        <!-- Y轴文字尺寸 -->
        <attr name="yTextSize" />
        <!-- Y轴文字位置 -->
        <attr name="yTextAlign" />
        <!-- 是否显示Y轴刻度线 -->
        <attr name="showYScaleLine" />
        <!-- Y轴刻度线颜色 -->
        <attr name="yScaleLineColor" />
        <!-- Y轴刻度线线宽 -->
        <attr name="yScaleLineWidth" />
        <!-- Y轴刻度线长度 -->
        <attr name="yScaleLineLength" />

        <!-- 是否显示XY轴箭头 -->
        <attr name="showAxisArrow" format="boolean" />
        <attr name="axisArrowWidth" format="dimension" />
        <attr name="axisArrowHeight" format="dimension" />
        <attr name="axisArrowColor" format="reference|color" />

        <attr name="limitLineColor" />
        <attr name="limitLineWidth" />
        <attr name="limitLineLength" />
        <attr name="limitLineSpace" />

        <!-- 折线宽度 -->
        <attr name="lineChartWidth" />
        <!-- 折线颜色 -->
        <attr name="lineChartColor" />
        <!-- 折线区域距离上边的距离 -->
        <attr name="lineChartPaddingTop" format="dimension" />
        <!-- 折线区域距离下边的距离 -->
        <attr name="lineChartPaddingBottom" format="dimension" />
        <!-- 折线区域距离左边的距离 -->
        <attr name="lineChartPaddingStart" />
        <!-- 背景颜色 -->
        <attr name="lineChartBgColor" />
        <!-- 绘制曲线 -->
        <attr name="drawCurve" />
        <!-- 绘制动画折线 -->
        <attr name="showLineChartAnim" format="boolean" />

        <!-- 是否显示折线点 -->
        <attr name="showLineChartPoint" format="boolean" />
        <!-- 折线点半径 -->
        <attr name="pointRadius" format="dimension" />
        <!-- 折线点颜色 -->
        <attr name="pointColor" format="reference|color" />
        <!-- 折线点描边宽 -->
        <attr name="pointStrokeWidth" format="dimension" />
        <!-- 折线点描边颜色 -->
        <attr name="pointStrokeColor" format="reference|color" />
        <!-- 折线点选中半径 -->
        <attr name="pointSelectedRadius" format="dimension" />
        <!-- 折线点选中颜色 -->
        <attr name="pointSelectedColor" format="reference|color" />
        <!-- 折线点选中描边宽 -->
        <attr name="pointSelectedStrokeWidth" format="dimension" />
        <!-- 折线点选中描边颜色 -->
        <attr name="pointSelectedStrokeColor" format="reference|color" />
        <!-- 折线点选中外描边宽度 -->
        <attr name="pointSelectedOutStrokeWidth" format="dimension" />
        <!-- 折线点选中外描边颜色 -->
        <attr name="pointSelectedOutStrokeColor" format="reference|color" />
        <!-- 折线点开始绘制的位置 -->
        <attr name="pointStartX" format="dimension" />
        <!-- 折线点结束绘制的位置 -->
        <attr name="pointEndX" format="dimension" />

        <!-- 是否显示折线点悬浮框 -->
        <attr name="showPointFloatBox" format="boolean" />
        <!-- 悬浮框颜色 -->
        <attr name="floatBoxColor" format="reference|color" />
        <!-- 悬浮框文本颜色 -->
        <attr name="floatBoxTextColor" format="reference|color" />
        <!-- 悬浮框文本尺寸 -->
        <attr name="floatBoxTextSize" format="dimension" />
        <!-- 悬浮框边距离文本的距离 -->
        <attr name="floatBoxPadding" format="dimension" />
    </declare-styleable>

    <declare-styleable name="LiveLineChartView">
        <!-- 是否显示Y轴 -->
        <attr name="showYAxis" />
        <!-- Y轴颜色 -->
        <attr name="yAxisColor"/>
        <!-- Y轴线宽 -->
        <attr name="yAxisWidth"/>
        <!-- 是否显示Y轴文字 -->
        <attr name="showYText"/>
        <!-- Y轴文字颜色 -->
        <attr name="yTextColor"/>
        <!-- Y轴文字尺寸 -->
        <attr name="yTextSize"/>
        <!-- Y轴文字位置 -->
        <attr name="yTextAlign"/>
        <!-- 是否显示Y轴刻度线 -->
        <attr name="showYScaleLine"/>
        <!-- Y轴刻度线颜色 -->
        <attr name="yScaleLineColor"/>
        <!-- Y轴刻度线线宽 -->
        <attr name="yScaleLineWidth" />
        <!-- Y轴刻度线长度 -->
        <attr name="yScaleLineLength" />

        <attr name="limitLineColor" />
        <attr name="limitLineWidth" />
        <attr name="limitLineLength" />
        <attr name="limitLineSpace" />

        <!-- 折线宽度 -->
        <attr name="lineChartWidth"/>
        <!-- 折线颜色 -->
        <attr name="lineChartColor"/>
        <!-- 折线区域距离左边的距离 -->
        <attr name="lineChartPaddingStart" />
        <!-- 背景颜色 -->
        <attr name="lineChartBgColor" />
        <!-- 绘制曲线 -->
        <attr name="drawCurve" />

        <attr name="limitLineCount" format="integer" />
        <attr name="pointSpace" format="dimension" />
    </declare-styleable>
```



# License

```
Copyright 2022 zicheng2019

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

