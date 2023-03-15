package uab.cs422.projectinlook.util

import android.content.Context

object ScreenConversion {
    fun dpToPx(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}