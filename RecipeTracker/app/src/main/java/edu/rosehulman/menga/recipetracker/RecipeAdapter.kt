package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*

class RecipeAdapter(var context: Context, val listener: OnRecipeSelectedListener, val uid: String): RecyclerView.Adapter<RecipeViewHolder>() {
    var recipes = ArrayList<Recipe>()
    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.RECIPES_PATH)

    //added because recyclerView sometimes does not populate until cursor is in title_text_view of the add dialog
    fun update() {
        recipeRef
            //.whereEqualTo("uid", uid).orderBy(Recipe.CREATION_KEY, Query.Direction.DESCENDING).get()
            .orderBy(Recipe.CREATION_KEY, Query.Direction.DESCENDING).whereEqualTo("uid", uid).get()
            .addOnSuccessListener {
            recipes.clear()
            for(doc in it.documents) {
                val recipe = Recipe.fromSnapshot(doc)
                recipes.add(recipe)
            }
            notifyDataSetChanged()
        }
    }

    init {
//        if(mine){
//            query = picRef.whereEqualTo("recipe", recipe)
//        }else{
//            query = picRef.orderBy(Recipe.CREATION_KEY, Query.Direction.ASCENDING)
//        }
        recipeRef
            .orderBy(Recipe.CREATION_KEY, Query.Direction.DESCENDING)
//            .whereEqualTo("uid", uid)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                Log.e(Constants.TAG, "Listen error: $exception")
                return@addSnapshotListener
            }
            for (documentChange in snapshot!!.documentChanges) {
                if(documentChange.document["uid"] != uid) {
                    return@addSnapshotListener
                }
                val recipe = Recipe.fromSnapshot(documentChange.document)
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> {
                        recipes.add(0, recipe)
                        notifyItemInserted(0)
                    }
                    DocumentChange.Type.REMOVED -> {
                        val pos = recipes.indexOfFirst { recipe.id == it.id }
                        recipes.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val pos = recipes.indexOfFirst { recipe.id == it.id }
                        recipes[pos] = recipe
                        notifyItemChanged(pos)
                    }
                }
            }
        }
        update()
    }

    fun showRecipe(position: Int) {
        listener.showRecipe(recipes[position])
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

    //the username will be tracked in the recipe object
    fun add(recipe: Recipe) {
        recipeRef.add(recipe)
    }

    interface OnRecipeSelectedListener {
        fun showRecipe(recipe: Recipe)
    }
}