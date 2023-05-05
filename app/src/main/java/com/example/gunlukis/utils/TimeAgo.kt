package com.example.gunlukis.utils

import java.util.*

class TimeAgo {


    companion object {

        private  val SECOND_MILLIS = 1000
        private  val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private  val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private  val DAY_MILLIS = 24 * HOUR_MILLIS

        private fun currentDate(): Date {
            val calendar = Calendar.getInstance()
            return calendar.time
        }


        fun getTimeAgo(date: Long): String {
            var time = date
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now = currentDate().time
            if (time > now || time <= 0) {
                return "in the future"
            }

            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> "şimdi"
                diff < 2 * MINUTE_MILLIS -> "1 dk"
                diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} dk"
                diff < 2 * HOUR_MILLIS -> "s"
                diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} s"
                diff < 48 * HOUR_MILLIS -> "dün"
                else -> "${diff / DAY_MILLIS} günler önce"
            }
        }
    }

}