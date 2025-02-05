package com.hontail.ui.cocktail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.databinding.ListItemCocktailSearchRecentBinding
import com.hontail.databinding.ListItemCocktailSearchResultBinding
import com.hontail.databinding.ListItemCocktailSearchSearchBarBinding
import com.hontail.databinding.ListItemCustomCocktailSearchResultBinding
import com.hontail.ui.mypage.Cocktail
import com.hontail.util.CocktailItemAdapter

class CocktailSearchAdapter(private val context: Context, private val items: List<CocktailSearchItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_RECENT = 1
        const val VIEW_TYPE_RESULT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CocktailSearchItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CocktailSearchItem.Recent -> VIEW_TYPE_RECENT
            is CocktailSearchItem.Result -> VIEW_TYPE_RESULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCocktailSearchSearchBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailSearchBarViewHolder(binding)
            }

            VIEW_TYPE_RECENT -> {
                val binding = ListItemCocktailSearchRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailRecentViewHolder(binding)
            }

            VIEW_TYPE_RESULT -> {
                val binding = ListItemCocktailSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailResultViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(val item = items[position]) {
            is CocktailSearchItem.SearchBar -> (holder as CocktailSearchBarViewHolder).bind()
            is CocktailSearchItem.Recent -> (holder as CocktailRecentViewHolder).bind(item.recentList)
            is CocktailSearchItem.Result -> (holder as CocktailResultViewHolder).bind(item.resultList)
        }
    }

    // Search Bar
    inner class CocktailSearchBarViewHolder(private val binding: ListItemCocktailSearchSearchBarBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

        }
    }

    // 최근 검색
    inner class CocktailRecentViewHolder(private val binding: ListItemCocktailSearchRecentBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(recentList: List<RecentItem>) {

            binding.apply {

                val cocktailSearchRecentAdapter = CocktailSearchRecentAdapter(recentList)

                recyclerViewListItemCocktailRecent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewListItemCocktailRecent.adapter = cocktailSearchRecentAdapter
            }
        }
    }

    // 칵테일 검색 결과
    inner class CocktailResultViewHolder(private val binding: ListItemCocktailSearchResultBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cocktailList: List<Cocktail>) {

            binding.apply {

                val cocktailSearchResultAdapter = CocktailItemAdapter(cocktailList)

                recyclerViewListItemCocktailSearchResult.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListItemCocktailSearchResult.adapter = cocktailSearchResultAdapter
            }
        }
    }
}