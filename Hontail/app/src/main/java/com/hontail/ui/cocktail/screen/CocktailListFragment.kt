package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.picture.screen.FilterBottomSheetFragment
import com.hontail.ui.cocktail.viewmodel.CocktailListFragmentViewModel
import com.hontail.ui.cocktail.adapter.CocktailListAdapter
import com.hontail.util.CommonUtils

private const val TAG = "CocktailListFragment_SSAFY"

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailListFragmentViewModel by viewModels()
    private val filters = mutableListOf("찜", "시간", "도수", "베이스주")

    private lateinit var cocktailListAdapter: CocktailListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUserId(activityViewModel.userId)
        initFilter()
        applySelectedFilters()
        viewModel.getCocktailFiltering()
    }

    private fun initFilter() {
        viewModel.baseSpirit = activityViewModel.selectedBaseFilter.value ?: ""
    }

    private fun applySelectedFilters() {
        val selectedFilters =
            viewModel.filterSelectedList.value ?: listOf(false, false, false, false)

        when {
            selectedFilters[0] -> { // 찜
                viewModel.orderBy = "likeCnt"
                viewModel.direction =
                    if (activityViewModel.selectedZzimFilter.value == 1) "DESC" else "ASC"
            }

            selectedFilters[1] -> { // 시간
                viewModel.orderBy = "createdAt"
                viewModel.direction =
                    if (activityViewModel.selectedTimeFilter.value == 1) "DESC" else "ASC"
            }

            selectedFilters[2] -> { // 도수
                viewModel.orderBy = "alcoholContent"
                viewModel.direction =
                    if (activityViewModel.selectedAlcoholFilter.value == 1) "DESC" else "ASC"
            }

            selectedFilters[3] -> { // 베이스주
                viewModel.orderBy = "id"
                viewModel.direction = "ASC"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(false)
        initAdapter()
        initData()
        initEvent()
    }

    private fun initAdapter() {
        binding.apply {
            val items = mutableListOf<CocktailListItem>().apply {
                add(CocktailListItem.SearchBar)
                add(CocktailListItem.TabLayout)
                add(CocktailListItem.Filter(filters))
                add(CocktailListItem.CocktailItems(emptyList()))
            }

            cocktailListAdapter = CocktailListAdapter(mainActivity, items)
            recyclerViewCocktailList.layoutManager =
                LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailList.adapter = cocktailListAdapter
        }
    }

    private fun initData() {
        activityViewModel.selectedBaseFilter.observe(viewLifecycleOwner) {
            viewModel.baseSpirit = it
            viewModel.getCocktailFiltering()
        }

        viewModel.cocktailList.observe(viewLifecycleOwner) { cocktailList ->
            cocktailList?.let {
                val updatedItems = mutableListOf<CocktailListItem>().apply {
                    add(CocktailListItem.SearchBar)
                    add(CocktailListItem.TabLayout)
                    add(CocktailListItem.Filter(filters))
                    add(CocktailListItem.CocktailItems(it))
                }
                cocktailListAdapter.updateItems(updatedItems)
            }
        }
    }

    private fun initEvent() {
        binding.apply {
            cocktailListAdapter.cocktailListListener =
                object : CocktailListAdapter.ItemOnClickListener {
                    override fun onClickRandom() {
                        val dialog = CocktailRandomDialogFragment()
                        dialog.show(parentFragmentManager, "CocktailRandomDialog")
                    }

                    override fun onClickCocktailItem(cocktailId: Int) {
                        activityViewModel.setCocktailId(cocktailId)
                        mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                    }

                    override fun onClickSearch() {
                        mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT)
                    }

                    override fun onClickTab(position: Int) {
                        when (position) {
                            0 -> {
                                viewModel.isCustom = false
                                resetFilters()
                            }

                            1 -> {
                                viewModel.isCustom = true
                                resetFilters()
                            }
                        }
                    }

                    override fun onClickFilter(position: Int) {
                        val bottomSheetFragment = FilterBottomSheetFragment.newInstance(position)
                        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                    }
                }
        }
    }

    private fun resetFilters() {
        viewModel.page = 0
        viewModel.direction = "ASC"
        viewModel.orderBy = "id"
        viewModel.baseSpirit = ""
        viewModel.getCocktailFiltering()
    }
}

sealed class CocktailListItem {
    object SearchBar : CocktailListItem()
    object TabLayout : CocktailListItem()
    data class Filter(val filters: List<String>) : CocktailListItem()
    data class CocktailItems(val cocktails: List<CocktailListResponse>) : CocktailListItem()
}