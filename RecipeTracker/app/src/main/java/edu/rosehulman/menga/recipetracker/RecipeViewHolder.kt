package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_recipe.view.*

class RecipeViewHolder: RecyclerView.ViewHolder {
    var context: Context

    constructor(itemView: View, adapter: RecipeAdapter, context: Context): super(itemView) {
        this.context = context
        itemView.setOnClickListener {
            adapter.showRecipe(adapterPosition)
        }
    }

    constructor(itemView: View, adapter: SearchAdapter, context: Context): super(itemView) {
        this.context = context
        itemView.setOnClickListener {
            adapter.showRecipe(adapterPosition)
        }
    }

    constructor(itemView: View, adapter: FavoriteAdapter, context: Context): super(itemView) {
        this.context = context
        itemView.setOnClickListener {
            adapter.showRecipe(adapterPosition)
        }
    }

    constructor(itemView: View, adapter: PopularAdapter, context: Context): super(itemView) {
        this.context = context
        itemView.setOnClickListener {
            adapter.showRecipe(adapterPosition)
        }
    }

    fun bind(recipe: Recipe) {
        itemView.title_recipe.text = recipe.title
    }
}