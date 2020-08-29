package id.ianahmfac.muvid.ui.home.fragments.bookmark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.adapters.ListVerticalAdapter
import id.ianahmfac.muvid.ui.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_bookmark.*

class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {
    private lateinit var viewModel: BookmarkViewModel
    private lateinit var verticalAdapter: ListVerticalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModelBookmark
        setupRecyclerView()

        verticalAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("result", it)
            }
            findNavController().navigate(R.id.action_bookmarkFragment_to_detailActivity, bundle)
        }

        viewModel.getBookmarkedMovies().observe(viewLifecycleOwner, Observer {
            verticalAdapter.showShimmer = false
            verticalAdapter.notifyDataSetChanged()
            if (it.isNotEmpty()) {
                verticalAdapter.differ.submitList(it)
                empty_anim.visibility = View.INVISIBLE
                text_anim.visibility = View.INVISIBLE
                btnBrowseMovie.visibility = View.INVISIBLE
            } else {
                verticalAdapter.differ.submitList(it)
                empty_anim.visibility = View.VISIBLE
                text_anim.visibility = View.VISIBLE
                btnBrowseMovie.visibility = View.VISIBLE
            }
        })

        btnBrowseMovie.setOnClickListener {
            findNavController().navigate(R.id.action_bookmarkFragment_to_searchFragment)
        }
    }

    private fun setupRecyclerView() {
        verticalAdapter = ListVerticalAdapter()
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvBookmark.apply {
            adapter = verticalAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(dividerItemDecoration)
        }
    }
}