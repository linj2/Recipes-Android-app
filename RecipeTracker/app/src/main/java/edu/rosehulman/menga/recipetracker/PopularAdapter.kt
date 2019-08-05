package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class PopularAdapter(var context: Context, val listener: RecipeAdapter.OnRecipeSelectedListener, val uid: String): RecyclerView.Adapter<RecipeViewHolder>() {
    val popularRecipes = ArrayList<Recipe>()

    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.RECIPES_PATH)

    //TODO: get most favorited list
    fun getPopularRecipes() {
        recipeRef.get().addOnSuccessListener {
            for(doc in it.documents) {
                val recipe = Recipe.fromSnapshot(doc)
                popularRecipes.add(recipe)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    override fun getItemCount() = popularRecipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, index: Int) {
        holder.bind(popularRecipes[index])
    }

    fun showRecipe(position: Int) {
        listener.showRecipe(popularRecipes[position], Constants.SEARCH, uid)
    }
}