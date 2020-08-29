package id.ianahmfac.muvid.ui.home.fragments.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.ianahmfac.muvid.R
import id.ianahmfac.muvid.adapters.ListVerticalAdapter
import id.ianahmfac.muvid.ui.home.HomeActivity
import id.ianahmfac.muvid.utils.Resources
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

@ExperimentalStdlibApi
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var viewModel: SearchViewModel
    private lateinit var listVerticalAdapter: ListVerticalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModelSearch

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) return true
                populateData(query, view)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun populateData(query: String, view: View) {
        setupRecyclerView()
        listVerticalAdapter.setOnItemClickListener { result ->
            val bundle = Bundle().apply {
                putSerializable("result", result)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_detailActivity,
                bundle
            )
        }

        viewModel.getSearch(query)
        viewModel.search.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgress()
                    response.data?.let {
                        if (it.results.isNotEmpty()) {
                            hideFileNotFound()
                            listVerticalAdapter.differ.submitList(it.results.toList())
                        } else {
                            showFileNotFound()
                        }
                    }
                }
                is Resources.Error -> {
                    hideProgress()
                    response.message?.let {
                        Snackbar.make(view, "An error occurred: $it", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgress()
                }
            }
        })
        text_search.text = getString(R.string.result_for, query.capitalize(Locale.ROOT))
    }

    private fun hideProgress() {
        listVerticalAdapter.apply {
            showShimmer = false
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        listVerticalAdapter.showShimmer = true
        searchAnim.visibility = View.INVISIBLE
        textSearchAnim.visibility = View.INVISIBLE
        btnGenres.visibility = View.INVISIBLE
        notFoundAnim.visibility = View.INVISIBLE
        textNotFound.visibility = View.INVISIBLE
    }

    private fun showFileNotFound() {
        notFoundAnim.visibility = View.VISIBLE
        textNotFound.visibility = View.VISIBLE
        rvSearch.visibility = View.INVISIBLE
    }

    private fun hideFileNotFound() {
        rvSearch.visibility = View.VISIBLE
        notFoundAnim.visibility = View.INVISIBLE
        textNotFound.visibility = View.INVISIBLE
    }

    private fun setupRecyclerView() {
        listVerticalAdapter = ListVerticalAdapter()
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvSearch.apply {
            adapter = listVerticalAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(dividerItemDecoration)
        }
    }
}