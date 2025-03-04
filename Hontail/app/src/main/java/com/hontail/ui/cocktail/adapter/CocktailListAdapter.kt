package com.hontail.ui.cocktail.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemCocktailListCocktailItemBinding
import com.hontail.databinding.ListItemCocktailListFilterBinding
import com.hontail.databinding.ListItemCocktailListSearchBarBinding
import com.hontail.databinding.ListItemCocktailListTabLayoutBinding
import com.hontail.ui.cocktail.screen.CocktailListItem
import com.hontail.util.CocktailItemAdapter

private const val TAG = "CocktailListAdapter_SSAFY"

class CocktailListAdapter(private val context: Context, private var items: MutableList<CocktailListItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cocktailListListener: ItemOnClickListener
    private var selectedFilterPosition: Int = -1

    interface ItemOnClickListener {
        fun onClickRandom()
        fun onClickCocktailItem(cocktailId: Int)
        fun onClickSearch()
        fun onClickTab(position: Int)
        fun onClickFilter(position: Int)
        fun onClickPageDown()
        fun onClickPageUp()
    }

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_TAB_LAYOUT = 1
        const val VIEW_TYPE_FILTER = 2
        const val VIEW_TYPE_COCKTAIL_ITEMS = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CocktailListItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CocktailListItem.TabLayout -> VIEW_TYPE_TAB_LAYOUT
            is CocktailListItem.Filter -> VIEW_TYPE_FILTER
            is CocktailListItem.CocktailItems -> VIEW_TYPE_COCKTAIL_ITEMS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCocktailListSearchBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListSearchBarViewHolder(binding)
            }
            VIEW_TYPE_TAB_LAYOUT -> {
                val binding = ListItemCocktailListTabLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListTabLayoutViewHolder(binding)
            }
            VIEW_TYPE_FILTER -> {
                val binding = ListItemCocktailListFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListFilterViewHolder(binding)
            }
            VIEW_TYPE_COCKTAIL_ITEMS -> {
                val binding = ListItemCocktailListCocktailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListCocktailItemsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is CocktailListItem.SearchBar -> (holder as CocktailListSearchBarViewHolder).bind()
            is CocktailListItem.TabLayout -> (holder as CocktailListTabLayoutViewHolder).bind()
            is CocktailListItem.Filter -> (holder as CocktailListFilterViewHolder).bind(item.filters, selectedFilterPosition)
            is CocktailListItem.CocktailItems -> (holder as CocktailListCocktailItemsViewHolder).bind(item.cocktails, item.currentPage, item.totalPage)
        }
    }

    // Search Bar
    inner class CocktailListSearchBarViewHolder(private val binding: ListItemCocktailListSearchBarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                // Cocktail 검색 화면으로 가기.
                constraintLayoutListItemCocktailListSearch.setOnClickListener {
                    cocktailListListener.onClickSearch()
                }

                // 랜덤 다이얼로그 띄우기.
                imageViewCocktailListRandom.setOnClickListener {
                    cocktailListListener.onClickRandom()
                }
            }
        }
    }

    // Tab Layout
    inner class CocktailListTabLayoutViewHolder(private val binding: ListItemCocktailListTabLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                tabLayoutCocktailList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        cocktailListListener.onClickTab(tab?.position ?: 0)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })
            }
        }
    }

    fun updateSelectedFilter(position: Int) {
        selectedFilterPosition = position
        Log.d(TAG, "HomeFilter adapter: ${selectedFilterPosition}")
        notifyItemChanged(2)  // FILTER의 위치 (Index 2)
    }

    // Filter
    inner class CocktailListFilterViewHolder(private val binding: ListItemCocktailListFilterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(filters: List<String>, selectedPosition: Int) {
            binding.apply {
                val cocktailListFilterAdapter = CocktailListFilterAdapter(filters, selectedPosition)

                recyclerViewListItemCocktailListFilter.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewListItemCocktailListFilter.adapter = cocktailListFilterAdapter

                cocktailListFilterAdapter.cocktailListFilterListener = object : CocktailListFilterAdapter.ItemOnClickListener {
                    override fun onClickFilter(position: Int) {
                        cocktailListListener.onClickFilter(position)
                    }
                }
            }
        }
    }

    // Cocktail Items
    inner class CocktailListCocktailItemsViewHolder(private val binding: ListItemCocktailListCocktailItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cocktails: List<CocktailListResponse>, currentPage: Int, totalPage: Int) {
            binding.apply {
                val cocktailListCocktailAdapter = CocktailItemAdapter(context, cocktails)

                recyclerViewListItemCocktailListCocktailItem.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListItemCocktailListCocktailItem.adapter = cocktailListCocktailAdapter

                // 칵테일 아이템 눌렀을 때 상세 화면으로
                cocktailListCocktailAdapter.cocktailItemListener = object : CocktailItemAdapter.ItemOnClickListener {
                    override fun onClickCocktailItem(cocktailId: Int) {
                        cocktailListListener.onClickCocktailItem(cocktailId)
                    }
                }

                // 페이지 다운
                imageViewListItemCocktailListCocktailPagePreview.setOnClickListener {
                    cocktailListListener.onClickPageDown()
                }

                // 페이지 업
                imageViewListItemCocktailListCocktailPageNext.setOnClickListener {
                    cocktailListListener.onClickPageUp()
                }

                textViewListItemCocktailListCocktailPage.text = "${currentPage + 1} / $totalPage"
            }
        }
    }

    fun updateItems(newItems: List<CocktailListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // 전체 갱신
    }

}