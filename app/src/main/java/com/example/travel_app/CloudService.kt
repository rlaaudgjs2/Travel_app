import android.os.Build
import androidx.annotation.RequiresApi
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

class CloudService {
    private val projectId = "travelapp-431208"
    private val bucketName = "travel_project"
    private lateinit var storage: Storage

    init {
        val credentials = GoogleCredentials.fromStream(FileInputStream("49db9a88775c9f5606a47abc398e0bb50b4519ca"))
        storage = StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build()
            .service
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadFile(filePath: String, objectName: String) {
        val blobId = BlobId.of(bucketName, objectName)
        val blobInfo = BlobInfo.newBuilder(blobId).build()
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun downloadFile(objectName: String, destinationPath: String) {
        val blob = storage.get(bucketName, objectName)
        blob.downloadTo(Paths.get(destinationPath))
    }

    fun deleteFile(objectName: String) {
        storage.delete(bucketName, objectName)
    }

    fun getFileUrl(objectName: String): String {
        return "https://storage.googleapis.com/$bucketName/$objectName"
    }
}