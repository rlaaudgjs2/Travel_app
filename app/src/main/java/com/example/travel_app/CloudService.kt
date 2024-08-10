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
import java.io.IOException
import java.nio.file.Files

class CloudService(private val context: Context) {
    private val projectId = "travelapp-431208"
    private val bucketName = "travel_project"
    private var storage: Storage? = null

    init {
        initializeStorage()
    }

    private fun initializeStorage() {
        try {
            val assetManager = context.assets
            val files = assetManager.list("")
            Log.d(TAG, "Assets files: ${files?.joinToString()}")

            val inputStream = context.assets.open("travelapp-431208-c6e1a65967de.json")
            val credentials = GoogleCredentials.fromStream(inputStream)
            storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .service
            Log.d(TAG, "CloudService initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing CloudService", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadFile(filePath: String, objectName: String): Boolean = withContext(Dispatchers.IO) {
        if (storage == null) {
            Log.e(TAG, "Storage is not initialized")
            return@withContext false
        }

        try {
            Log.d(TAG, "Attempting to upload file: $filePath to $objectName")
            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: $filePath")
                return@withContext false
            }

            val blobId = BlobId.of(bucketName, objectName)
            val blobInfo = BlobInfo.newBuilder(blobId).build()
            val bytes = Files.readAllBytes(file.toPath())
            Log.d(TAG, "File size: ${bytes.size} bytes")

            val blob = storage?.create(blobInfo, bytes)
            Log.d(TAG, "File uploaded successfully: ${blob?.name}, size: ${blob?.size} bytes")
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading file", e)
            return@withContext false
        }
    }

    fun getFileUrl(objectName: String): String {
        return "https://storage.googleapis.com/$bucketName/$objectName"
    }

    companion object {
        private const val TAG = "CloudService"
    }
}