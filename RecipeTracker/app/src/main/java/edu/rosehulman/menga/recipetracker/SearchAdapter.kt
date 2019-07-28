package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore

class SearchAdapter(var context: Context, val listener: RecipeAdapter.OnRecipeSelectedListener): RecyclerView.Adapter<RecipeViewHolder>() {
    val recipes = ArrayList<Recipe>()

    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.RECIPES_PATH)

    init {
        recipeRef.get().addOnSuccessListener {
            for(doc in it.documents) {
                recipes.add(Recipe.fromSnapshot(doc))
            }
        }
    }

    fun executeSearch(query: String) {
        for(r in recipes) {
            //if it isn't close, remove it from recipes. then the adapter will be attached
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, index: Int) {
        holder.bind(recipes[index])
    }

    fun showRecipe(position: Int) {
        listener.showRecipe(recipes[position])
    }
}