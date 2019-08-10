package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FavoriteAdapter(var context: Context, val listener: RecipeAdapter.OnRecipeSelectedListener, val user: FirebaseUser): RecyclerView.Adapter<RecipeViewHolder>() {
    val recipes = ArrayList<Recipe>()
    val recipesRef = FirebaseFirestore.getInstance().collection(Constants.USERS_PATH)

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    init {
        recipesRef.whereEqualTo("favoriteOf", user.uid).get().addOnSuccessListener {
            for(doc in it.documents) {
                val recipe = Recipe.fromSnapshot(doc)
                var unique = true
                for(r in recipes) {
                    if(r.equals(recipe)) {
                        unique = false
                        remove(recipe)
                        break
                    }
                }
                if(unique) {
                    recipes.add(recipe)
                }
            }
            notifyDataSetChanged()
        }
    }

    fun remove(r: Recipe) {
        recipesRef.document(r.id).delete()
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(viewHolder: RecipeViewHolder, index: Int) {
        viewHolder.bind(recipes[index])
    }

    fun showRecipe(index: Int) {
        listener.showRecipe(recipes[index], Constants.FAVORITE, user)
    }

}