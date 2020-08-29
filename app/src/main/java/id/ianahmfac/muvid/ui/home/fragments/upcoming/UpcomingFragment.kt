package id.ianahmfac.muvid.ui.home.fragments.upcoming

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.adapters.UpcomingAdapter
import id.ianahmfac.muvid.ui.home.HomeActivity
import id.ianahmfac.muvid.utils.Constants
import id.ianahmfac.muvid.utils.Resources
import kotlinx.android.synthetic.main.fragment_upcoming.*

class UpcomingFragment : Fragment(R.layout.fragment_upcoming) {

    private lateinit var viewModel: UpcomingViewModel
    private lateinit var upcomingAdapter: UpcomingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModelUpcoming
        setupRecyclerView()

        upcomingAdapter.setOnItemClickListener { result ->
            val bundle = Bundle().apply {
                putSerializable("result", result)
            }
            findNavController().navigate(R.id.action_upcomingFragment_to_detailActivity, bundle)
        }

        viewModel.listUpcoming.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        upcomingAdapter.differ.submitList(it.results.toList())
                    }
                }

                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Snackbar.make(view, "An error occurred: $it", Snackbar.LENGTH_LONG).show()
                    }
                }

                is Resources.Loading -> showProgressBar()
            }
        })
    }

    private fun hideProgressBar() {
        upcomingAdapter.apply {
            showShimmer = false
            notifyDataSetChanged()
        }
        isLoading = false
    }

    private fun showProgressBar() {
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
            val firstItemPosition = layoutManager.findFirstVisibleItemPositions(intArrayOf(0, 1))
            val itemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading and !isLastPage
            val isAtLastItem =
                firstItemPosition[0] + itemCount >= totalItemCount && firstItemPosition[1] + itemCount >= totalItemCount
            val isNotAtBeginning = firstItemPosition[0] > 0 && firstItemPosition[1] > 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_SIZE_PAGE
            val shouldPaginate =
                isNotLoadingAndNotLastPage and isAtLastItem and isNotAtBeginning and isTotalMoreThanVisible and isScrolling

            if (shouldPaginate) {
                viewModel.getUpcoming()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }

    private fun setupRecyclerView() {
        upcomingAdapter = UpcomingAdapter()
        rvUpcoming.apply {
            adapter = upcomingAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(scrollListener)
        }
    }
}