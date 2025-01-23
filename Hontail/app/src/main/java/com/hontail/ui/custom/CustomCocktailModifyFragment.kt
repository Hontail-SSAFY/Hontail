package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailBinding
import com.hontail.databinding.FragmentCustomCocktailModifyBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CustomCocktailModifyFragment : BaseFragment<FragmentCustomCocktailModifyBinding>(
    FragmentCustomCocktailModifyBinding::bind,
    R.layout.fragment_custom_cocktail_modify
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}