package com.example.newsprojectpractice.ui.fragments

import android.app.ProgressDialog.show
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.adapters.NewsAdapter
import com.example.newsprojectpractice.databinding.FragmentFavouritesBinding
import com.example.newsprojectpractice.ui.NewsActivity
import com.example.newsprojectpractice.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavouritesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouritesBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setupFavouritesRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment, bundle)
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recycleView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, directions: Int){
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticles(article)
                Snackbar.make(view, "Removed from favourites", Snackbar.LENGTH_LONG).apply{
                    setAction("Undo") {
                        newsViewModel.addToFavourites(article)
                    }
                show()
            }
        }
    }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }

        newsViewModel.getFavouritesNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }
    private fun setupFavouritesRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}