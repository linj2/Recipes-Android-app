package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*

class RecipeAdapter(var context: Context, val uid: String): RecyclerView.Adapter<RecipeViewHolder>() {
    var recipes = ArrayList<Recipe>()
    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.RECIPES_PATH)

    init {
        //TODO: add argument to check user for my recipes
//        if(mine){
//            query = picRef.whereEqualTo("uid", uid)
//        }else{
//            query = picRef.orderBy(Recipe.CREATION_KEY, Query.Direction.ASCENDING)
//        }
        recipeRef
            .orderBy(Recipe.CREATION_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Log.e(Constants.TAG, "Listen error: $exception")
                return@addSnapshotListener
            }
            for (picChange in snapshot!!.documentChanges) {
                val pic = Recipe.fromSnapshot(picChange.document)
                when (picChange.type) {
                    DocumentChange.Type.ADDED -> {
                        recipes.add(0, pic)
                        notifyItemInserted(0)
                    }
                    DocumentChange.Type.REMOVED -> {
                        val pos = recipes.indexOfFirst { pic.id == it.id }
                        recipes.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val pos = recipes.indexOfFirst { pic.id == it.id }
                        recipes[pos] = pic
                        notifyItemChanged(pos)
                    }
                }
            }
        }
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
        recipeRef.add(recipe)
    }

    interface onRecipeSelectedListener {

    }
}