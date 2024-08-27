import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateUtility {
    private const val INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    private const val OUTPUT_FORMAT = "yyyy년 M월 d일 HH:mm"

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return "날짜 없음"
        }
        return try {
            val inputFormat = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("DateUtility", "Date parsing error", e)
            "날짜 형식 오류"
        }
    }

    fun getCurrentFormattedDate(): String {
        return try {
            val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())
            outputFormat.format(Date())
        } catch (e: Exception) {
            Log.e("DateUtility", "Error formatting current date", e)
            "날짜 형식 오류"
        }
    }

    fun formatDateOrGetCurrent(dateString: String?): String {
        return if (dateString.isNullOrEmpty()) {
            getCurrentFormattedDate()
        } else {
            formatDate(dateString)
        }
    }
}