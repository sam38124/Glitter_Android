package com.jianzhi.glitter.util

class JzClock{
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    var past = sdf.parse(sdf.format(Date()))

    fun zeroing(){
        past = sdf.parse(sdf.format(Date()))
    }
    fun stop():Double{
        val now = sdf.parse(sdf.format(Date()))

        return getDatePoor(now,past)
    }
    fun getDatePoor(endDate: Date, nowDate: Date): Double {
        val diff = endDate.time - nowDate.time
        val sec = diff.toDouble() / 1000
        return sec.toDouble()
    }
}