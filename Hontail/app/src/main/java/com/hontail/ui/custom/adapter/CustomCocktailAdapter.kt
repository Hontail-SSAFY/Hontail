package com.hontail.ui.custom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.databinding.ListItemCustomCocktailEmptyBinding
import com.hontail.databinding.ListItemCustomCocktailExistBinding
import com.hontail.ui.custom.screen.CustomCocktailItem

class CustomCocktailAdapter(private var items: MutableList<CustomCocktailItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var customCocktailIngredientListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickDelete(position: Int)
    }

    companion object {

        const val VIEW_TYPE_INGREDIENT = 0
        const val VIEW_TYPE_EMPTY = 1
    }

    override fun getItemViewType(position: Int): Int {

        return when(items[position]) {
            is CustomCocktailItem.IngredientItem -> VIEW_TYPE_INGREDIENT
            is CustomCocktailItem.EmptyItem -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_INGREDIENT -> {
                val binding = ListItemCustomCocktailExistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailExistViewHolder(binding)
            }

            VIEW_TYPE_EMPTY -> {
                val binding = ListItemCustomCocktailEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailEmptyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {
            is CustomCocktailExistViewHolder -> holder.bind(items[position] as CustomCocktailItem.IngredientItem, position)
            is CustomCocktailEmptyViewHolder -> holder.bind()
        }
    }

    fun updateItems(newItems: MutableList<CustomCocktailItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // 재료가 채워졌을 때
    inner class CustomCocktailExistViewHolder(private val binding: ListItemCustomCocktailExistBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailItem.IngredientItem, position: Int) {

            binding.apply {
                textViewListItemCustomCocktailExistIngredientName.text = item.ingredientName
                textViewListItemCustomCocktailExistIngredientQuantity.text = item.ingredientQuantity

                Glide.with(root.context)
                    .load(item.ingredientImage)
                    .into(imageViewListItemCustomCocktailExistIngredient)

                imageViewListItemCustomCocktailExistDelete.setOnClickListener {
                    customCocktailIngredientListener.onClickDelete(position)
                }
            }
        }
    }

    // 비어있을 때 ViewHolder
    inner class CustomCocktailEmptyViewHolder(private val binding: ListItemCustomCocktailEmptyBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {
                textViewListItemCustomCocktailEmptyTitle.text = "칵테일의 시작은 재료 선택부터!"
                textViewListItemCustomCocktailEmptySubTitle.text = "재료를 골라 나만의 칵테일을 공유하세요."
            }
        }
    }
}