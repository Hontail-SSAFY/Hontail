package com.hontail.ui.ingredient

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentIngredientListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import org.threeten.bp.temporal.TemporalAmount

class IngredientListFragment : BaseFragment<FragmentIngredientListBinding>(
    FragmentIngredientListBinding::bind,
    R.layout.fragment_ingredient_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        ingredientAdapter = IngredientAdapter()
        recyclerView = binding.recyclerViewIngredient
        recyclerView.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(IngredientItemDecoration())
        }
    }

    private fun loadDummyData() {
        val dummyIngredients = listOf(
            Ingredient("감미료 및 시럽", null),
            Ingredient("신선한 과일 및 장식", null),
            Ingredient("주스 및 퓌레", null),
            Ingredient("베이스 주류", null),
            Ingredient("맥주 및 사이다", null),
            Ingredient("향신료 및 조미료", null),
            Ingredient("비터스", null),
            Ingredient("차 및 인퓨전", null),
            Ingredient("무알코올 음료", null),
            Ingredient("기타", null),
        )
        ingredientAdapter.submitList(dummyIngredients)
    }

    inner class IngredientItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val density = resources.displayMetrics.density
            val spacingInPixels = (32 * density).toInt()
            outRect.bottom = spacingInPixels
        }
    }
}

data class Ingredient(
    val name: String, val amount: String?
)