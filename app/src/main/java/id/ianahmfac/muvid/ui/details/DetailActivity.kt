package id.ianahmfac.muvid.ui.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.adapters.VideosAdapter
import id.ianahmfac.muvid.data.db.MovieDatabase
import id.ianahmfac.muvid.data.models.detail.DetailMovieResponse
import id.ianahmfac.muvid.data.repository.MovieRepository
import id.ianahmfac.muvid.utils.Resources
import id.ianahmfac.muvid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val args: DetailActivityArgs by navArgs()
    private lateinit var videosAdapter: VideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupRecyclerViewVideos()

        val result = args.result
        var state: Boolean = false

        val movieRepository = MovieRepository(MovieDatabase(this))
        val factory = ViewModelFactory(movieRepository, this.application)
        val viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        viewModel.getDetail(result.id)
        viewModel.getMovie(result.id).observe(this, Observer { movie ->
            if (movie != null) {
                state = movie.bookmarked
                setBookmark(state)
            }
        })

        viewModel.detail.observe(this, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    response.data?.let {
                        populateData(it)
                    }
                    loadingAnim.visibility = View.INVISIBLE
                }
                is Resources.Loading -> {
                    loadingAnim.visibility = View.VISIBLE
                }
            }
        })

        btnSaved.setOnClickListener {
            state = if (state) {
                viewModel.deleteMovie(result)
                Snackbar.make(it, "Removed from movie bookmark", Snackbar.LENGTH_SHORT).show()
                false
            } else {
                viewModel.savedMovie(result)
                Snackbar.make(it, "Added to movie bookmark", Snackbar.LENGTH_SHORT).show()
                true
            }
            setBookmark(state)
        }
    }

    private fun setBookmark(state: Boolean) {
        if (state) {
            btnSaved.icon = ContextCompat.getDrawable(this, R.drawable.ic_saved_movie)
            btnSaved.text = "Saved"
        } else {
            btnSaved.icon = ContextCompat.getDrawable(this, R.drawable.ic_save_movie)
            btnSaved.text = "Save"
        }
    }

    private fun populateData(detail: DetailMovieResponse) {
        Glide.with(this).load(detail.getBackdropPath()).apply(
            RequestOptions.placeholderOf(R.drawable.placeholder_poster_movie)
                .error(R.drawable.placeholder_poster_movie)
        ).into(imgBackdropDetail)
        Glide.with(this).load(detail.getPosterPath()).apply(
            RequestOptions.placeholderOf(R.drawable.placeholder_poster_movie)
                .error(R.drawable.placeholder_poster_movie)
        ).into(imgPosterDetail)
        titleDetail.text = detail.title
        expand_text_view.text = detail.overview
        tagLineDetail.text = detail.tagline

        val sbGenre = StringBuilder()
        for (i in detail.genres.indices) {
            sbGenre.append(detail.genres[i].name + ", ")
        }
        genreDetail.text = replaceComma(sbGenre.toString())
        releaseDateDetail.text = detail.release_date
        runtimeDetail.text = "${detail.runtime}m"
        statusDetail.text = detail.status

        val movieTrailer = detail.videos.results
        if (movieTrailer.isNotEmpty()) {
            text_videos.visibility = View.VISIBLE
            imgPlayTrailer.visibility = View.VISIBLE
            imgPlayTrailer.setOnClickListener { watchTrailer(movieTrailer[0].key) }
            videosAdapter.differ.submitList(movieTrailer)
            videosAdapter.setOnItemClickListener {
                watchTrailer(it.key)
            }
        }

        val homepage = detail.homepage
        if (homepage.equals("") || homepage == null) {
            btnHomepage.visibility = View.GONE
        } else {
            btnHomepage.setOnClickListener { goToBrowserHomepage(homepage) }
        }
    }

    private fun watchTrailer(key: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$key"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$key"))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    private fun goToBrowserHomepage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun replaceComma(str: String): String {
        return if (str.isEmpty()) str
        else str.substring(0, str.lastIndexOf(", "))
    }

    private fun setupRecyclerViewVideos() {
        videosAdapter = VideosAdapter()
        rvVideos.apply {
            layoutManager =
                LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = videosAdapter
        }
        videosAdapter.notifyDataSetChanged()
    }
}