package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.viewmodel.CocktailListFragmentViewModel
import com.hontail.ui.cocktail.adapter.CocktailListAdapter
import com.hontail.util.CommonUtils
import com.hontail.util.DialogToLoginFragment

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
    private var selectedFilterPosition: Int = -1

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
        val selectedFilters = activityViewModel.filterSelectedList
        Log.d(TAG, "Filter applySelectedFilters: ${selectedFilters.value}")
        changeFilter(selectedFilters.value ?: listOf(false, false, false, false))
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        activityViewModel.setCocktailId(1)
        applySelectedFilters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(false)
        initAdapter()
        initData()
        initEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityViewModel.setBaseFromHome(false)
    }

    private fun initAdapter() {
        binding.apply {
            val items = mutableListOf<CocktailListItem>().apply {
                add(CocktailListItem.SearchBar)
                add(CocktailListItem.TabLayout)
                add(CocktailListItem.Filter(filters))
                add(CocktailListItem.CocktailItems(emptyList(), 0, 0))
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
        }

        activityViewModel.filterSelectedList.observe(viewLifecycleOwner) {
            changeFilter(it)
            updateFilterUI()
            viewModel.getCocktailFiltering()
        }

        viewModel.cocktailList.observe(viewLifecycleOwner) { cocktailList ->
            cocktailList?.let {
                val updatedItems = mutableListOf<CocktailListItem>().apply {
                    add(CocktailListItem.SearchBar)
                    add(CocktailListItem.TabLayout)
                    add(CocktailListItem.Filter(filters))
                    add(CocktailListItem.CocktailItems(it, viewModel.page, viewModel.totalPage))
                }
                cocktailListAdapter.updateItems(updatedItems)
                binding.recyclerViewCocktailList.smoothScrollToPosition(0)
            }
        }

        Log.d(TAG, "HomeFilter: ${activityViewModel.isBaseFromHome.value}")

        if (activityViewModel.isBaseFromHome.value == true) {
            selectedFilterPosition = 3
            cocktailListAdapter.updateSelectedFilter(selectedFilterPosition)
        }

        Log.d(TAG, "Likes initData: ${viewModel.userId.value}")
        viewModel.getUserCocktailLikesCnt()
    }

    fun changeFilter(selectedFilter: List<Boolean>) {
        val firstTrueIndex = selectedFilter.indexOf(true)
        when (firstTrueIndex) {
            0 -> { // 좋아요
                viewModel.orderBy = "likesCount"
                viewModel.direction =
                    if (activityViewModel.selectedZzimFilter.value == 1) "DESC" else "ASC"
            }

            1 -> { // 시간
                viewModel.orderBy = "createdAt"
                viewModel.direction =
                    if (activityViewModel.selectedTimeFilter.value == 1) "DESC" else "ASC"
            }

            2 -> { // 도수
                viewModel.orderBy = "alcoholContent"
                viewModel.direction =
                    if (activityViewModel.selectedAlcoholFilter.value == 1) "DESC" else "ASC"
            }

            3 -> {
                viewModel.orderBy = "id"
                viewModel.direction = "ASC"
            }
        }
    }

    private fun initEvent() {
        binding.apply {
            cocktailListAdapter.cocktailListListener =
                object : CocktailListAdapter.ItemOnClickListener {
                    override fun onClickRandom() {
                        if (activityViewModel.userId == 0) {
                            // 비로그인 상태일 때 로그인 다이얼로그 표시
                            val dialog = DialogToLoginFragment()
                            dialog.show(parentFragmentManager, "DialogToLoginFragment")
                        } else {
                            if (viewModel.cocktailLikesCnt.value!! > 0){
                                val dialog = CocktailRandomDialogFragment()
                                dialog.show(parentFragmentManager, "CocktailRandomDialog")
                            }else{
                                Toast.makeText(mainActivity, "찜한 칵테일을 추가해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
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
                                cocktailListAdapter.updateSelectedFilter(-1)
                            }

                            1 -> {
                                viewModel.isCustom = true
                                resetFilters()
                                cocktailListAdapter.updateSelectedFilter(-1)
                            }
                        }
                    }

                    override fun onClickFilter(position: Int) {
                        val bottomSheetFragment = FilterBottomSheetFragment.newInstance(position)
                        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                        selectedFilterPosition = position
                    }

                    override fun onClickPageDown() {
                        if (viewModel.page > 0) {
                            viewModel.page -= 1
                            viewModel.getCocktailFiltering()
                        }
                    }

                    override fun onClickPageUp() {
                        if (viewModel.page < viewModel.totalPage - 1) {
                            viewModel.page += 1
                            viewModel.getCocktailFiltering()
                        }
                    }
                }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true){
                    override fun handleOnBackPressed() {
                        mainActivity.changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)

                        mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_home
                    }
                })
        }
    }

    private fun updateFilterUI() {
        cocktailListAdapter.updateSelectedFilter(selectedFilterPosition)
    }

    private fun resetFilters() {
        viewModel.page = 0
        viewModel.direction = "ASC"
        viewModel.orderBy = "id"
        viewModel.baseSpirit = ""
        activityViewModel.setFilterClear()
        viewModel.getCocktailFiltering()
    }
}

sealed class CocktailListItem {
    object SearchBar : CocktailListItem()
    object TabLayout : CocktailListItem()
    data class Filter(val filters: List<String>) : CocktailListItem()
    data class CocktailItems(
        val cocktails: List<CocktailListResponse>,
        val currentPage: Int,
        val totalPage: Int
    ) : CocktailListItem()
}