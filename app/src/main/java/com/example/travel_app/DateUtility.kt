import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateUtility {
    private const val INPUT_FORMAT = "yyyy-MM-dd HH:mm"
    private const val OUTPUT_FORMAT = "yyyy-MM-dd" // 'YYYY-MM-DD' 형식으로 수정

    /**
     * 입력된 날짜 문자열을 INPUT_FORMAT에서 OUTPUT_FORMAT으로 변환합니다.
     * @param dateString 입력 날짜 문자열
     * @return 변환된 날짜 문자열 또는 오류 메시지
     */
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
            Log.e("DateUtility", "날짜 파싱 오류", e)
            "날짜 형식 오류"
        }
    }

    /**
     * 현재 날짜를 OUTPUT_FORMAT 형식으로 반환합니다.
     * @return 'YYYY-MM-DD' 형식으로 포맷된 현재 날짜 문자열
     */
    fun getCurrentFormattedDate(): String {
        return try {
            val timeZone = TimeZone.getTimeZone("Asia/Seoul") // 한국 시간대 설정
            val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())
            outputFormat.timeZone = timeZone
            outputFormat.format(Date())
        } catch (e: Exception) {
            Log.e("DateUtility", "현재 날짜 포맷 오류", e)
            "날짜 형식 오류"
        }
    }

    /**
     * 입력된 날짜 문자열을 변환하거나, 입력이 비어 있으면 현재 날짜를 반환합니다.
     * @param dateString 입력 날짜 문자열
     * @return 변환된 날짜 문자열 또는 'YYYY-MM-DD' 형식의 현재 날짜
     */
    fun formatDateOrGetCurrent(dateString: String?): String {
        return if (dateString.isNullOrEmpty()) {
            getCurrentFormattedDate()
        } else {
            formatDate(dateString)
        }
    }
}
