package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

private const val TAG = "CustomCocktailFragment"
class CustomCocktailFragment : BaseFragment<FragmentCustomCocktailBinding>(
    FragmentCustomCocktailBinding::bind,
    R.layout.fragment_custom_cocktail
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailAdapter: CustomCocktailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)
        initToolbar()
        initAdapter()
        initEvent()
//        testRoom()
    }

    // 툴바 설정
    private fun initToolbar() {
        binding.apply {
            toolbarCustomCocktail.apply {
                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    // 백스택에서 이전 fragment 확인
                    val count = parentFragmentManager.backStackEntryCount
                    if (count > 1) {
                        val previousEntry = parentFragmentManager.getBackStackEntryAt(count - 2)
                        // 이전 화면에 따라 bottom navigation 아이템 체크
                        when (previousEntry.name) {
                            "HomeFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_home
                            "CocktailListFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_search
                            "ZzimFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_heart
                            "MyPageFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_mypage
                        }
                    }
                    parentFragmentManager.popBackStack("CustomCocktailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<CustomCocktailItem>().apply {
                add(CustomCocktailItem.IngredientItem("오렌지", "1/2 개"))
                add(CustomCocktailItem.IngredientItem("에스프레소", "20 ml"))
            }

            val items2 = mutableListOf<CustomCocktailItem>().apply {
                if(isIngredientListEmpty()) {
                    add(CustomCocktailItem.EmptyItem)
                }
            }

            customCocktailAdapter = CustomCocktailAdapter(items2)

            recyclerViewCustomCocktail.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktail.adapter = customCocktailAdapter
        }
    }

    // 재료가 있는지 확인
    private fun isIngredientListEmpty(): Boolean {
        return true
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 재료 삭제
            customCocktailAdapter.customCocktailIngredientDeleteListener = object : CustomCocktailAdapter.ItemOnClickListener {
                override fun onClick() {
                    TODO("Not yet implemented")
                }
            }

            // 재료 추가
            fabCustomCocktailIngredientAdd.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT)
            }

            // 다음으로 넘어가기
            buttonCustomCocktailNext.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT)
            }
        }
    }

    // Room DB 테스트 코드
//    private fun testRoom() {
//
//        CoroutineScope(Dispatchers.Main).launch {
//
//            val ingredientList = ingredientRepository.getIngredients()
//
//            Log.d(TAG, "testRoom: ${ingredientList.size}")
//
//            for(ingredient in ingredientList) {
//                Log.d(TAG, "ingredientName: ${ingredient.ingredientNameKor}")
//            }
//        }
//    }

}

sealed class CustomCocktailItem {

    data class IngredientItem(val ingredientName: String, val ingredientQuantity: String): CustomCocktailItem()
    object EmptyItem: CustomCocktailItem()
}