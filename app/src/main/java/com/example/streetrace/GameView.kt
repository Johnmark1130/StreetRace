package com.example.streetrace

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class GameView(var c: Context, var gameTask: MainActivity) : View(c) {
    private var myPaint: Paint? = null
    private var Speed = 2
    private var time = 0
    private var score = 0
    private var Car = 0
    private var otherCars = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0

    init {
        myPaint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 + Speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }
        time = time + 10 + Speed

        val carWidth = viewWidth / 5
        val carHeight = viewHeight / 10

        val playerCarRect = Rect(
            Car * viewWidth / 2 + viewWidth / 15 + 28,
            viewHeight - 2 - carHeight,
            Car * viewWidth / 2 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )

        myPaint!!.color = Color.BLUE
        canvas.drawRect(playerCarRect, myPaint!!)

        var highScore = 0

        for (i in otherCars.indices) {
            try {
                val carX = otherCars[i]["lane"] as Int * viewWidth / 4 - viewWidth / 25
                var carY = time - otherCars[i]["startTime"] as Int

                val otherCarRect = Rect(
                    carX + 20, carY - carHeight, carX + carWidth - 20, carY
                )

                if (Rect.intersects(playerCarRect, otherCarRect)) {
                    gameTask.closeGame(score)
                    return
                }

                myPaint!!.color = Color.GREEN
                canvas.drawRect(otherCarRect, myPaint!!)

                if (carY > viewHeight + carHeight) {
                    otherCars.removeAt(i)
                    score++
                    Speed = 2 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score: $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed: $Speed", 380f, 80f, myPaint!!)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (Car > 0) {
                        Car--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (Car < 2) {
                        Car++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }
}
