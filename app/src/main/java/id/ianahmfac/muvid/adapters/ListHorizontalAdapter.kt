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
import id.ianahmfac.muvid.data.models.list.Result
import kotlinx.android.synthetic.main.item_now_playing.view.imgPoster
import kotlinx.android.synthetic.main.item_now_playing.view.tvTitle
import kotlinx.android.synthetic.main.item_top_rated.view.*

class ListHorizontalAdapter : RecyclerView.Adapter<ListHorizontalAdapter.TopRatedViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    var showShimmer: Boolean = true
    private val shimmerItemNumber = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedViewHolder {
        return TopRatedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_top_rated, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return if (showShimmer)
            shimmerItemNumber
        else
            differ.currentList.size
    }

    override fun onBindViewHolder(holder: TopRatedViewHolder, position: Int) {
        if (showShimmer) {
            holder.itemView.shimmerHorizontal.startShimmer()
        } else {
            val movie = differ.currentList[position]
            holder.itemView.apply {
                shimmerHorizontal.stopShimmer()
                shimmerHorizontal.setShimmer(null)
                Glide.with(this).load(movie.getPosterPath()).apply(
                    RequestOptions.placeholderOf(R.drawable.placeholder_poster_movie)
                        .error(R.drawable.placeholder_poster_movie)
                ).into(imgPoster)
                tvTitle.text = movie.title
                tvTitle.background = null

                setOnClickListener {
                    onItemClickListener?.let { it(movie) }
                }
            }
        }
    }

    inner class TopRatedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }
}