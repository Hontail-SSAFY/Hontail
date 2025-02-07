package com.hontail.ui.cocktail

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailDetailIngredientItemBinding
import com.hontail.ui.ingredient.Ingredient

class CocktailDetailIngredientAdapter(private val items: List<Ingredient>): RecyclerView.Adapter<CocktailDetailIngredientAdapter.CocktailDetailIngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailDetailIngredientViewHolder {
        val binding = ListItemCocktailDetailIngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailDetailIngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailDetailIngredientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailDetailIngredientViewHolder(private val binding: ListItemCocktailDetailIngredientItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ingredient) {

            binding.apply {

                textViewCocktailDetailIngredientName.text = item.name
                textViewCocktailDetailIngredientAmount.text = item.amount
            }
        }
    }
}