import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

class CloudService(private val context: Context) {
    private val projectId = "travelapp-431208"
    private val bucketName = "travel_project"
    private var storage: Storage? = null

    init {
        try {
            val assetManager = context.assets
            val files = assetManager.list("")
            Log.d("CloudService", "Assets files: ${files?.joinToString()}")

            val inputStream = context.assets.open("travelapp-431208-c6e1a65967de.json")
            val credentials = GoogleCredentials.fromStream(inputStream)
            storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .service
        } catch (e: Exception) {
            Log.e("CloudService", "Error initializing CloudService", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadFile(filePath: String, objectName: String) = withContext(Dispatchers.IO) {
        if (storage == null) {
            Log.e("CloudService", "Storage is not initialized")
            return@withContext
        }
        try {
            Log.d("CloudService", "Attempting to upload file: $filePath to $objectName")
            val file = File(filePath)
            if (!file.exists()) {
                Log.e("CloudService", "File does not exist: $filePath")
                return@withContext
            }
            val blobId = BlobId.of(bucketName, objectName)
            val blobInfo = BlobInfo.newBuilder(blobId).build()
            val bytes = Files.readAllBytes(file.toPath())
            Log.d("CloudService", "File size: ${bytes.size} bytes")
            val blob = storage?.create(blobInfo, bytes)
            Log.d("CloudService", "File uploaded successfully: ${blob?.name}, size: ${blob?.size} bytes")
        } catch (e: Exception) {
            Log.e("CloudService", "Error uploading file", e)
        }
    }

    fun downloadFile(objectName: String, destinationPath: String) {
        if (storage == null) {
            Log.e("CloudService", "Storage is not initialized")
            return
        }
        // 다운로드 로직 구현
    }

    fun deleteFile(objectName: String) {
        if (storage == null) {
            Log.e("CloudService", "Storage is not initialized")
            return
        }
        // 삭제 로직 구현
    }

    fun getFileUrl(objectName: String): String {
        return "https://storage.googleapis.com/$bucketName/$objectName"
    }
}