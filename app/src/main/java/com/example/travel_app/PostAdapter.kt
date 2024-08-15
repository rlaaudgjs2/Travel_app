import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.ImageAdapter
import com.example.travel_app.R
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(private var posts: List<PostResponse>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val nickNameTextView: TextView = view.findViewById(R.id.nickNameTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val imagesRecyclerView: RecyclerView = view.findViewById(R.id.imagesRecyclerView)
        val hashtagChipGroup: ChipGroup = view.findViewById(R.id.hashtagChipGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bulletin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.titleTextView.text = post.title
        holder.nickNameTextView.text = post.nickName
        holder.dateTextView.text = formatDate(post.creationDate)

        // 이미지 목록 바인딩
        val imageAdapter = ImageAdapter(post.imageUrls)
        holder.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        // 해시태그 목록 바인딩
        holder.hashtagChipGroup.removeAllViews()
        post.hashtagList.forEach { hashtag ->
            val chip = Chip(holder.itemView.context)
            chip.text = hashtag
            holder.hashtagChipGroup.addView(chip)
        }
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<PostResponse>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return "날짜 없음"
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy년 M월 d일 HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("PostAdapter", "Date parsing error", e)
            "날짜 형식 오류"
        }
    }
}