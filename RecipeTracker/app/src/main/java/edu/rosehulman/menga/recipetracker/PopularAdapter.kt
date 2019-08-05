package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.*

class PopularAdapter(var context: Context, val listener: RecipeAdapter.OnRecipeSelectedListener, val uid: String): RecyclerView.Adapter<RecipeViewHolder>() {
    val popularRecipes = ArrayList<Pair<Recipe, Int>>()

    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_PATH)

    fun getPopularRecipes() {
        recipeRef.get().addOnSuccessListener {
            popularRecipes.clear()
            for(doc in it.documents) {
                add(Recipe.fromSnapshot(doc))
            }
            popularRecipes.sortByDescending {
                it.second
            }
            notifyDataSetChanged()
        }
    }

    fun add(recipe: Recipe) {
        for((i, p) in popularRecipes.withIndex()) {
            if(p.first.equals(recipe)) {
                popularRecipes[i] = Pair(p.first, 1+p.second)
                return
            }
        }
        popularRecipes.add(popularRecipes.size, Pair(recipe, 0))
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    override fun getItemCount() = popularRecipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, index: Int) {
        holder.bind(popularRecipes[index].first)
    }

    fun showRecipe(position: Int) {
        listener.showRecipe(popularRecipes[position].first, Constants.POPULAR, uid)
    }
}