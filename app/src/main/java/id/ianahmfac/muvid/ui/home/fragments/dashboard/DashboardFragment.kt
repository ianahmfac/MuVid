package id.ianahmfac.muvid.ui.home.fragments.dashboard

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.adapters.ListHorizontalAdapter
import id.ianahmfac.muvid.adapters.ListVerticalAdapter
import id.ianahmfac.muvid.ui.home.HomeActivity
import id.ianahmfac.muvid.utils.Constants.QUERY_SIZE_PAGE
import id.ianahmfac.muvid.utils.Resources
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var listVerticalAdapter: ListVerticalAdapter
    private lateinit var listHorizontalAdapter: ListHorizontalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModel
        setupTopRatedRecyclerView()
        setupNowPlayingRecyclerView()

        listVerticalAdapter.setOnItemClickListener { result ->
            val bundle = Bundle().apply {
                putSerializable("result", result)
            }
            findNavController().navigate(R.id.action_dashboardFragment_to_detailActivity, bundle)
        }

        listHorizontalAdapter.setOnItemClickListener { result ->
            val bundle = Bundle().apply {
                putSerializable("result", result)
            }
            findNavController().navigate(R.id.action_dashboardFragment_to_detailActivity, bundle)
        }

        viewModel.listTopRated.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgress()
                    response.data?.let {
                        listHorizontalAdapter.differ.submitList(it.results.toList())
                    }
                }

                is Resources.Error -> {
                    hideProgress()
                    response.message?.let {
                        Snackbar.make(view, "An Error Occurred: $it", Snackbar.LENGTH_LONG).show()
                    }
                }

                is Resources.Loading -> showProgress()
            }
        })

        viewModel.listNowPlaying.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgress()
                    response.data?.let {
                        listVerticalAdapter.differ.submitList(it.results.toList())
                    }
                }

                is Resources.Error -> {
                    hideProgress()
                    response.message?.let {
                        Snackbar.make(view, "An Error Occurred: $it", Snackbar.LENGTH_LONG).show()
                    }
                }

                is Resources.Loading -> {
                    showProgress()
                }
            }
        })
    }

    private fun hideProgress() {
        listVerticalAdapter.showShimmer = false
        listVerticalAdapter.notifyDataSetChanged()
        listHorizontalAdapter.showShimmer = false
        listHorizontalAdapter.notifyDataSetChanged()
        isLoading = false
    }

    private fun showProgress() {
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
            val itemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading and !isLastPage
            val isAtLastItem = firstItemPosition + itemCount >= totalItemCount
            val isNotAtBeginning = firstItemPosition > 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_SIZE_PAGE
            val shouldPaginate =
                isNotLoadingAndNotLastPage and isAtLastItem and isNotAtBeginning and isTotalMoreThanVisible and isScrolling

            if (shouldPaginate) {
                viewModel.getNowPlaying()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }

    private fun setupNowPlayingRecyclerView() {
        listVerticalAdapter = ListVerticalAdapter()
        val dividerItemDecoration =
            DividerItemDecoration(rvNowPlaying.context, DividerItemDecoration.VERTICAL)
        rvNowPlaying.apply {
            adapter = listVerticalAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(dividerItemDecoration)
            addOnScrollListener(scrollListener)
        }
    }

    private fun setupTopRatedRecyclerView() {
        listHorizontalAdapter = ListHorizontalAdapter()
        rvTopRated.apply {
            adapter = listHorizontalAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}