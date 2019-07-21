package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class RecipeAdapter(var context: Context, val uid: String): RecyclerView.Adapter<RecipeViewHolder>() {
    var recipes = ArrayList<Recipe>()

    init {
        //TODO: add a listener to the backend and
    }

    fun showRecipe(position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, index: Int) {
        holder.bind(recipes[index])
    }

    //TODO: add these to the backend
    //the username will be tracked in the recipe object
    fun add(recipe: Recipe) {
        recipes.add(recipe)
        notifyDataSetChanged()
    }

    interface onRecipeSelectedListener {

    }
}