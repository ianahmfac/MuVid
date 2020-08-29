package id.ianahmfac.muvid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.data.models.detail.ResultVideo
import kotlinx.android.synthetic.main.item_videos_detail.view.*

class VideosAdapter : RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ResultVideo>() {
        override fun areItemsTheSame(oldItem: ResultVideo, newItem: ResultVideo): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ResultVideo, newItem: ResultVideo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        return VideosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_videos_detail, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val videos = differ.currentList[position]
        holder.itemView.apply {
            val urlThumbnail = "https://img.youtube.com/vi/${videos.key}/0.jpg"
            Glide.with(this).load(urlThumbnail).apply(
                RequestOptions.placeholderOf(R.drawable.placeholder_poster_movie)
                    .error(R.drawable.placeholder_poster_movie)
            ).into(thumbnailVideo)
            titleVideo.text = videos.name
            playTrailerVideo.setOnClickListener {
                onItemClickListener?.let { it(videos) }
            }
        }
    }

    inner class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((ResultVideo) -> Unit)? = null

    fun setOnItemClickListener(listener: (ResultVideo) -> Unit) {
        onItemClickListener = listener
    }
}