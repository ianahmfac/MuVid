package id.ianahmfac.muvid.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.data.db.MovieDatabase
import id.ianahmfac.muvid.data.repository.MovieRepository
import id.ianahmfac.muvid.ui.home.fragments.bookmark.BookmarkViewModel
import id.ianahmfac.muvid.ui.home.fragments.dashboard.DashboardViewModel
import id.ianahmfac.muvid.ui.home.fragments.search.SearchViewModel
import id.ianahmfac.muvid.ui.home.fragments.upcoming.UpcomingViewModel
import id.ianahmfac.muvid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    lateinit var viewModel: DashboardViewModel
    lateinit var viewModelUpcoming: UpcomingViewModel
    lateinit var viewModelSearch: SearchViewModel
    lateinit var viewModelBookmark: BookmarkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val movieRepository = MovieRepository(MovieDatabase(this))
        val factory = ViewModelFactory(movieRepository, application)
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        viewModelUpcoming = ViewModelProvider(this, factory)[UpcomingViewModel::class.java]
        viewModelSearch = ViewModelProvider(this, factory)[SearchViewModel::class.java]
        viewModelBookmark = ViewModelProvider(this, factory)[BookmarkViewModel::class.java]

        bottomNavigation.setupWithNavController(movieNavHostFragment.findNavController())
    }
}