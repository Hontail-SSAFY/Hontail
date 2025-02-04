package com.hontail.ui.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import androidx.core.content.res.ResourcesCompat

class IngredientAdapter : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {
    private var ingredients = listOf<Ingredient>()

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView as TextView

        init {
            val density = itemView.context.resources.displayMetrics.density
            val heightInPixels = (20 * density).toInt()

            nameTextView.apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    heightInPixels
                )
                typeface = ResourcesCompat.getFont(context, R.font.suit_semibold)
                setTextColor(context.getColor(R.color.basic_white))
                textSize = 16f
            }
        }

        fun bind(ingredient: Ingredient) {
            nameTextView.text = ingredient.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int = ingredients.size

    fun submitList(newList: List<Ingredient>) {
        ingredients = newList
        notifyDataSetChanged()
    }
}