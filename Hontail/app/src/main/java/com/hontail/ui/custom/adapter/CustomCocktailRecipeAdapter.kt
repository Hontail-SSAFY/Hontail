package com.hontail.ui.custom.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.databinding.ListItemCustomCocktailRecipeAddStepBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeAlcoholLevelBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeDescriptionBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeImageBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeNameBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeRegisterBinding
import com.hontail.databinding.ListItemCustomCocktailRecipeStepBinding
import com.hontail.ui.custom.screen.CocktailRecipeStep
import com.hontail.ui.custom.screen.CustomCocktailRecipeItem
import okhttp3.internal.notify

class CustomCocktailRecipeAdapter(private val context: Context, private val items: MutableList<CustomCocktailRecipeItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var customCocktailRecipeListener: ItemOnClickListener

    var selectedImageUri: Uri? = null

    interface ItemOnClickListener {
        fun onClickRegister()
        fun onClickAddStep()
        fun onClickDeleteStep(position: Int)
        fun onClickAddImage()
    }

    companion object {
        const val VIEW_TYPE_IMAGE = 0
        const val VIEW_TYPE_NAME = 1
        const val VIEW_TYPE_ALCOHOL_LEVEL = 2
        const val VIEW_TYPE_DESCRIPTION = 3
        const val VIEW_TYPE_RECIPE_STEP = 4
        const val VIEW_TYPE_RECIPE_ADD_STEP = 5
        const val VIEW_TYPE_RECIPE_REGISTER = 6
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CustomCocktailRecipeItem.CustomCocktailRecipeImage -> VIEW_TYPE_IMAGE
            is CustomCocktailRecipeItem.CustomCocktailRecipeName -> VIEW_TYPE_NAME
            is CustomCocktailRecipeItem.CustomCocktailAlcoholLevel -> VIEW_TYPE_ALCOHOL_LEVEL
            is CustomCocktailRecipeItem.CustomCocktailDescription -> VIEW_TYPE_DESCRIPTION
            is CustomCocktailRecipeItem.CustomCocktailRecipeStep -> VIEW_TYPE_RECIPE_STEP
            is CustomCocktailRecipeItem.CustomCocktailRecipeAddStep -> VIEW_TYPE_RECIPE_ADD_STEP
            is CustomCocktailRecipeItem.CustomCocktailRecipeRegister -> VIEW_TYPE_RECIPE_REGISTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_IMAGE -> {
                val binding = ListItemCustomCocktailRecipeImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeImageViewHolder(binding)
            }

            VIEW_TYPE_NAME -> {
                val binding = ListItemCustomCocktailRecipeNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeNameViewHolder(binding)
            }

            VIEW_TYPE_ALCOHOL_LEVEL -> {
                val binding = ListItemCustomCocktailRecipeAlcoholLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeAlcoholLevelViewHolder(binding)
            }

            VIEW_TYPE_DESCRIPTION -> {
                val binding = ListItemCustomCocktailRecipeDescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeDescriptionViewHolder(binding)
            }

            VIEW_TYPE_RECIPE_STEP -> {
                val binding = ListItemCustomCocktailRecipeStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeStepViewHolder(binding)
            }

            VIEW_TYPE_RECIPE_ADD_STEP -> {
                val binding = ListItemCustomCocktailRecipeAddStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeAddStepViewHolder(binding)
            }

            VIEW_TYPE_RECIPE_REGISTER -> {
                val binding = ListItemCustomCocktailRecipeRegisterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailRecipeRegisterViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val item = items[position]) {
            is CustomCocktailRecipeItem.CustomCocktailRecipeImage -> (holder as CustomCocktailRecipeImageViewHolder).bind(item)
            is CustomCocktailRecipeItem.CustomCocktailRecipeName -> (holder as CustomCocktailRecipeNameViewHolder).bind(item)
            is CustomCocktailRecipeItem.CustomCocktailAlcoholLevel -> (holder as CustomCocktailRecipeAlcoholLevelViewHolder).bind(item)
            is CustomCocktailRecipeItem.CustomCocktailDescription -> (holder as CustomCocktailRecipeDescriptionViewHolder).bind(item)
            is CustomCocktailRecipeItem.CustomCocktailRecipeStep -> (holder as CustomCocktailRecipeStepViewHolder).bind(item.recipeStepList)
            is CustomCocktailRecipeItem.CustomCocktailRecipeAddStep -> (holder as CustomCocktailRecipeAddStepViewHolder).bind()
            is CustomCocktailRecipeItem.CustomCocktailRecipeRegister -> (holder as CustomCocktailRecipeRegisterViewHolder).bind()
        }
    }

    fun updateItems(newItems: List<CustomCocktailRecipeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // 완성된 칵테일 사진
    inner class CustomCocktailRecipeImageViewHolder(private val binding: ListItemCustomCocktailRecipeImageBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailRecipeItem.CustomCocktailRecipeImage) {

            binding.apply {

                if(selectedImageUri != null) {
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.GONE
                    imageViewListItemCustomCocktailRecipeImage.visibility = View.VISIBLE
                    imageViewListItemCustomCocktailRecipeImage.setImageURI(item.imageUri)
                }
                else {
                    imageViewListItemCustomCocktailRecipeImage.setImageResource(R.color.basic_gray)
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.VISIBLE
                }

                constraintLayoutListItemCustomCocktailRecipeImageGuide.setOnClickListener {
                    customCocktailRecipeListener.onClickAddImage()
                }

                imageViewListItemCustomCocktailRecipeImage.setOnClickListener {
                    customCocktailRecipeListener.onClickAddImage()
                }

            }
        }
    }

    // 이름
    inner class CustomCocktailRecipeNameViewHolder(private val biding: ListItemCustomCocktailRecipeNameBinding): RecyclerView.ViewHolder(biding.root) {

        fun bind(item: CustomCocktailRecipeItem.CustomCocktailRecipeName) {

        }
    }

    // 도수
    inner class CustomCocktailRecipeAlcoholLevelViewHolder(private val binding: ListItemCustomCocktailRecipeAlcoholLevelBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailRecipeItem.CustomCocktailAlcoholLevel) {

            binding.apply {
                textViewListItemCustomCocktailRecipeAlcoholLevelAlcoholLevel.text = "${item.alcoholLevel} %"
            }
        }
    }

    // 칵테일 설명
    inner class CustomCocktailRecipeDescriptionViewHolder(private val binding: ListItemCustomCocktailRecipeDescriptionBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(customCocktailDescription: CustomCocktailRecipeItem.CustomCocktailDescription) {

            binding.apply {
            }
        }
    }

    // 제조 방법 레시피 단계
    inner class CustomCocktailRecipeStepViewHolder(private val binding: ListItemCustomCocktailRecipeStepBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(recipeStepList: MutableList<CocktailRecipeStep>) {

            binding.apply {

                val customCocktailRecipeStepAdapter = CustomCocktailRecipeStepAdapter(recipeStepList)

                recyclerViewListItemCustomCocktailRecipeStep.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewListItemCustomCocktailRecipeStep.adapter = customCocktailRecipeStepAdapter
                recyclerViewListItemCustomCocktailRecipeStep.isNestedScrollingEnabled = false

                customCocktailRecipeStepAdapter.customCocktailRecipeStepListener = object : CustomCocktailRecipeStepAdapter.ItemOnClickListener {

                    // 레시피 단계 삭제
                    override fun onClickDelete(position: Int) {
                        customCocktailRecipeListener.onClickDeleteStep(position)
                    }
                }
            }
        }
    }

    // 제조 방법 레시피 추가
    inner class CustomCocktailRecipeAddStepViewHolder(private val binding: ListItemCustomCocktailRecipeAddStepBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {

                imageViewListItemCustomCocktailRecipeAddStep.setOnClickListener {
                    customCocktailRecipeListener.onClickAddStep()
                }
            }
        }
    }

    // 레시피 등록
    inner class CustomCocktailRecipeRegisterViewHolder(private val binding: ListItemCustomCocktailRecipeRegisterBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {

                imageViewListItemCustomCocktailRecipeAddStep.setOnClickListener {
                    customCocktailRecipeListener.onClickRegister()
                }
            }
        }
    }
}