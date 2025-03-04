package com.hontail.ui

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.hontail.R
import com.hontail.base.BaseActivity
import com.hontail.databinding.ActivityMainBinding
import com.hontail.ui.alarm.AlarmFragment
import com.hontail.ui.bartender.screen.BartenderFragment
import com.hontail.ui.custom.screen.CustomCocktailBottomSheetFragment
import com.hontail.ui.custom.screen.CustomCocktailFragment
import com.hontail.ui.custom.screen.CustomCocktailIngredientDetailFragment
import com.hontail.ui.custom.screen.CustomCocktailRecipeFragment
import com.hontail.ui.custom.screen.CustomCocktailSearchFragment
import com.hontail.ui.cocktail.screen.CocktailDetailFragment
import com.hontail.ui.cocktail.screen.CocktailListFragment
import com.hontail.ui.cocktail.screen.CocktailRecipeFragment
import com.hontail.ui.cocktail.screen.CocktailSearchFragment
import com.hontail.ui.home.screen.HomeFragment
import com.hontail.ui.ingredient.screen.IngredientAddFragment
import com.hontail.ui.mypage.screen.MyPageFragment
import com.hontail.ui.mypage.screen.MyPageModifyFragment
import com.hontail.ui.mypage.screen.MyPageNicknameModifyFragment
import com.hontail.ui.picture.screen.CocktailTakePictureFragment
import com.hontail.ui.cocktail.screen.FilterBottomSheetFragment
import com.hontail.ui.picture.screen.CocktailPictureResultFragment
import com.hontail.ui.profile.ProfileFragment
import com.hontail.ui.zzim.screen.ZzimFragment
import com.hontail.util.CommonUtils
import com.hontail.util.PermissionChecker
import com.hontail.util.DialogToLoginFragment
import io.grpc.netty.shaded.io.netty.util.internal.logging.CommonsLoggerFactory

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val activityViewModel: MainActivityViewModel by viewModels()

    /** permission check **/
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT

        checkPermissions()
        initData()
        initBottomNavigation()
        initBackStackListener()
        changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
    }

    private fun initBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            // 현재 표시되는 Fragment 확인
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutMainFragment)

            // Fragment 타입에 따라 bottom navigation 아이템 선택
            when (currentFragment) {
                is HomeFragment -> setSelectedBottomNavigation(R.id.navigation_home)
                is CocktailListFragment -> setSelectedBottomNavigation(R.id.navigation_search)
                is ZzimFragment -> setSelectedBottomNavigation(R.id.navigation_heart)
                is MyPageFragment -> setSelectedBottomNavigation(R.id.navigation_mypage)
            }
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // 로그인이 필요한 메뉴 아이디 리스트
            val requireLoginMenus = listOf(
                R.id.navigation_plus,
                R.id.navigation_heart,
                R.id.navigation_mypage
            )

            when (item.itemId) {
                R.id.navigation_home -> {
                    changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
                    true
                }
                R.id.navigation_search -> {
                    if (activityViewModel.isBaseFromHome.value == true){
                        changeFragment(CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT)
                        activityViewModel.setFilterSelectedList(listOf(false, false, false, true))
                    }else{
                        activityViewModel.setFilterClear()
                        changeFragment(CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT)
                        activityViewModel.setFilterSelectedList(listOf(false, false, false, false))
                    }

                    true
                }
                // 로그인이 필요한 메뉴들
                R.id.navigation_plus,
                R.id.navigation_heart,
                R.id.navigation_mypage -> {
                    if (activityViewModel.userId == 0) {
                        // 로그인 다이얼로그 표시
                        val dialog = DialogToLoginFragment()
                        dialog.show(supportFragmentManager, "DialogToLoginFragment")

                        // 현재 선택된 탭 유지
                        false
                    } else {
                        // 로그인된 상태면 해당 페이지로 이동
                        when (item.itemId) {
                            R.id.navigation_plus -> {
                                changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT)
                            }
                            R.id.navigation_heart -> {
                                changeFragment(CommonUtils.MainFragmentName.ZZIM_FRAGMENT)
                            }
                            R.id.navigation_mypage -> {
                                changeFragment(CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT)
                            }
                        }
                        true
                    }
                }
                else -> false
            }
        }
    }

    fun setSelectedBottomNavigation(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    fun initData(){
        val userId = intent.getIntExtra("user_id", 0)
        activityViewModel.userId = userId

        val userNickname = intent.getStringExtra("user_nickname")
        activityViewModel.userNickname = userNickname ?: ""
    }

    fun checkPermissions() {
        /* permission check */
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.requestPermissionLauncher.launch(runtimePermissions)
        }
    }

    fun changeFragmentWithCallback(
        fragmentName: CommonUtils.MainFragmentName,
        callback: () -> Unit
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

        when (fragmentName) {
            CommonUtils.MainFragmentName.HOME_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, HomeFragment())
            }
            CommonUtils.MainFragmentName.ALARM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, AlarmFragment())
                    .addToBackStack("AlarmFragment")
            }
            CommonUtils.MainFragmentName.BARTENDER_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, BartenderFragment())
                    .addToBackStack("bartenderFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailDetailFragment())
                    .addToBackStack("CocktailDetailFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailListFragment())
            }
            CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailRecipeFragment())
                    .addToBackStack("CocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailSearchFragment())
                    .addToBackStack("CocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailFragment())
                    .addToBackStack("CustomCocktailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailSearchFragment())
                    .addToBackStack("CustomCocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_INGREDIENT_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailIngredientDetailFragment())
                    .addToBackStack("CustomCocktailIngredientDetailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_BOTTOM_SHEET_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailBottomSheetFragment())
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailRecipeFragment())
                    .addToBackStack("CustomCocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientAddFragment())
                    .addToBackStack("IngredientAddFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageModifyFragment())
                    .addToBackStack("MyPageModifyFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_NICKNAME_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageNicknameModifyFragment())
                    .addToBackStack("MyPageNicknameModifyFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailPictureResultFragment())
                    .addToBackStack("CocktailPictureResultFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailTakePictureFragment())
                    .addToBackStack(null)
            }
            CommonUtils.MainFragmentName.PROFILE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ProfileFragment())
                    .addToBackStack("ProfileFragment")
            }
            CommonUtils.MainFragmentName.ZZIM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ZzimFragment())
            }
            CommonUtils.MainFragmentName.FILTERBOTTOMSHEETFRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, FilterBottomSheetFragment())
            }
        }

        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        callback.invoke()
    }

    private var currentFragment : CommonUtils.MainFragmentName? = null

    fun changeFragment(name: CommonUtils.MainFragmentName) {
        val transaction = supportFragmentManager.beginTransaction()

        val enterAnim = when {
            currentFragment == null -> R.anim.anim_slide_in_from_right_fade_in
            currentFragment!!.ordinal < name.ordinal -> R.anim.anim_slide_in_from_right_fade_in
            else -> R.anim.anim_slide_in_from_left_fade_in
        }

        transaction.setCustomAnimations(enterAnim, 0)

        when (name) {
            CommonUtils.MainFragmentName.HOME_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, HomeFragment())
            }
            CommonUtils.MainFragmentName.ALARM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, AlarmFragment())
                    .addToBackStack("AlarmFragment")
            }
            CommonUtils.MainFragmentName.BARTENDER_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, BartenderFragment())
                    .addToBackStack("bartenderFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT -> {

                transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

                transaction.replace(R.id.frameLayoutMainFragment, CocktailDetailFragment())
                    .addToBackStack("CocktailDetailFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailListFragment())
            }
            CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailRecipeFragment())
                    .addToBackStack("CocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailSearchFragment())
                    .addToBackStack("CocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailFragment())
                    .addToBackStack("CustomCocktailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailSearchFragment())
                    .addToBackStack("CustomCocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_INGREDIENT_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailIngredientDetailFragment())
                    .addToBackStack("CustomCocktailIngredientDetailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_BOTTOM_SHEET_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailBottomSheetFragment())
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailRecipeFragment())
                    .addToBackStack("CustomCocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientAddFragment())
                    .addToBackStack("IngredientAddFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageModifyFragment())
                    .addToBackStack("MyPageModifyFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_NICKNAME_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageNicknameModifyFragment())
                    .addToBackStack("MyPageNicknameModifyFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailPictureResultFragment())
                    .addToBackStack("CocktailPictureResultFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailTakePictureFragment())
                    .addToBackStack(null)
            }
            CommonUtils.MainFragmentName.PROFILE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ProfileFragment())
                    .addToBackStack("ProfileFragment")
            }
            CommonUtils.MainFragmentName.ZZIM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ZzimFragment())
            }
            CommonUtils.MainFragmentName.FILTERBOTTOMSHEETFRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, FilterBottomSheetFragment())
            }
        }

        transaction.commit()
        currentFragment = name
    }

    fun hideBottomNav(state: Boolean) {
        if (state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }
}