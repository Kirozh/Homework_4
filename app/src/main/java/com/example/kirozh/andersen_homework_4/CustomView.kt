package com.example.kirozh.andersen_homework_4

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Kirill Ozhigin on 07.10.2021
 */
class CustomView @JvmOverloads constructor (
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0,
        ) : View(context, attr, defStyle) {


    // время по умолчанию
    private var hours = 12
    private var minutes = 0
    private var seconds = 0

    // значения для круга
    private var centerH = 0
    private var centerW = 0
    private var radius = 300

    // угол стрелки относительно полудня
    private var hoursAngle: Float = hours.toFloat() * 30 + minutes.toFloat() / 2 + seconds.toFloat() / 120
    private var minutesAngle: Float = 6 * minutes.toFloat() + seconds.toFloat() / 10
    private var secondsAngle:Float = seconds.toFloat() * 6

    // массив делений циферблата
    private var divisionArray = MutableList(12){ _ -> Division(0,0,0,0f) }

    //аттрибуты
    private val attributes: TypedArray = context.obtainStyledAttributes(attr, R.styleable.CustomViewAttrs)
    private val mSecondLineColor = attributes.getColor(R.styleable.CustomViewAttrs_secondLineColor, Color.RED)
    private val mMinuteLineColor = attributes.getColor(R.styleable.CustomViewAttrs_minuteLineColor, Color.BLACK)
    private val mHourLineColor = attributes.getColor(R.styleable.CustomViewAttrs_hourLineColor, Color.BLACK)
    private val mSecondLineLength = attributes.getDimensionPixelSize(R.styleable.CustomViewAttrs_secondLineLength, 150)
    private val mMinuteLineLength = attributes.getDimensionPixelSize(R.styleable.CustomViewAttrs_minuteLineLength, 120)
    private val mHourLineLength = attributes.getDimensionPixelSize(R.styleable.CustomViewAttrs_hourLineLength, 80)

    //    var hourLineLength = 180
    //    var minuteLineLength = 220
    //    var secondLineLength = 250

    private var hourLineLength = mHourLineLength
    private var minuteLineLength = mMinuteLineLength
    private var secondLineLength = mSecondLineLength

    private val circlePaint:Paint = Paint().apply{

        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    private val centerPointPaint = Paint().apply{

        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val divisionPaint = Paint().apply{
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 20f
    }

    private val secondPaint = Paint().apply{

        color = mSecondLineColor
        //color = Color.RED
        isAntiAlias = true
        strokeWidth = 4f
    }

    private val minutePaint = Paint().apply{
        color = mMinuteLineColor
        //color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 6f
    }

    private val hourPaint = Paint().apply{
        color = mHourLineColor
        //color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 10f
    }

    private val decimalPaint = Paint().apply{
        color = Color.BLACK
        textSize = 42f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerW = width/2
        centerH = height/2

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerW.toFloat(), centerH.toFloat(), 15f, centerPointPaint)
        canvas.drawCircle(centerW.toFloat(), centerH.toFloat(), 300f, circlePaint)

        drawNums(canvas)

        divisionArrayInit()

        drawDivisions(canvas)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))

        hours = calendar.get(Calendar.HOUR)
        minutes = calendar.get(Calendar.MINUTE)
        seconds = calendar.get(Calendar.SECOND)

        hoursAngle = hours * 30 + minutes / 2 + seconds.toFloat() / 120
        minutesAngle = 6 * minutes.toFloat() + seconds.toFloat() / 10
        secondsAngle = seconds.toFloat() * 6

        canvas.drawLine(centerW.toFloat(),
                        centerH.toFloat(),
                    centerW + secondLineLength*sin(secondsAngle*PI/180).toFloat(),
                    centerH - secondLineLength*cos(secondsAngle*PI/180).toFloat(),
                        secondPaint)

        canvas.drawLine(centerW.toFloat(),
                        centerH.toFloat(),
                    centerW + minuteLineLength*sin(minutesAngle*PI/180).toFloat(),
                    centerH - minuteLineLength*cos(minutesAngle*PI/180).toFloat(),
                        minutePaint)

        canvas.drawLine(centerW.toFloat(),
                        centerH.toFloat(),
                        centerW + hourLineLength * sin(hoursAngle*PI/180).toFloat(),
                        centerH - hourLineLength * cos(hoursAngle*PI/180).toFloat(),
                        hourPaint)

        invalidate()

    }

    // рисование цифр
    private fun drawNums(canvas: Canvas){
        val hoursNum = 1..12
        for (i in hoursNum){
            val num = i.toString()
            val decimalX = centerW + (radius - 60) * sin(30*i*PI/180)
            val decimalY = centerH - (radius - 60) * cos(30*i*PI/180)
            canvas.drawText(num, (decimalX - 14f).toFloat(), (decimalY+18f).toFloat(), decimalPaint)
        }
    }

    // рисование делений
    private fun drawDivisions(canvas: Canvas){
        for (i:Int in 0..11){
            canvas.drawLine(divisionArray[i].xStart,
                divisionArray[i].yStart,
                divisionArray[i].xEnd,
                divisionArray[i].yEnd,
                divisionPaint)
        }
    }

    private fun divisionArrayInit(){
        val hours = 0..11
        for (i in hours){
            divisionArray[i] = Division(centerW, centerH, radius, (30*PI/180*i).toFloat())
        }
    }
    // класс описывающий деления циферблата
    data class Division(val x:Int, val y:Int, val radius:Int, val angle:Float){
        var xStart:Float = (x + (radius-30) * cos(angle))
        var yStart: Float = (y + (radius-30) * sin(angle))
        var xEnd: Float = (x + radius * cos(angle))
        var yEnd: Float = (y + radius * sin(angle))



    }


}