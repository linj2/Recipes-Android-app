package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SearchAdapter(var context: Context, val listener: RecipeAdapter.OnRecipeSelectedListener, val user: FirebaseUser): RecyclerView.Adapter<RecipeViewHolder>() {
    val recipes = ArrayList<Pair<Recipe, Int>>()

    val recipeRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.RECIPES_PATH)

    //counts matches for searching later on
    fun match(r: Recipe, q: String): Int {
        if(q == "") return 1
        val t = r.title
        val queryList = ArrayList<String>()
        var cur = ""
        for(c in q) {
            if(c == ' ') {
                queryList.add(cur)
                cur = ""
            }
            else {
                if(c.isLetterOrDigit()) cur += c
            }
        }
        if(cur!="") queryList.add(cur)
        var matches = 0
        for(word in queryList) if(t.contains(word, true)) matches++
        return matches
    }

    fun executeSearch(query: String) {
        recipeRef.get().addOnSuccessListener {
            var i = 0
            for(doc in it.documents) {
                val recipe = Recipe.fromSnapshot(doc)
                i++
                val matches = match(recipe, query)
                if(matches>0) recipes.add(Pair(recipe, matches))
            }
            recipes.sortByDescending {
                it.second
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false)
        return RecipeViewHolder(view, this, context)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, index: Int) {
        holder.bind(recipes[index].first)
    }

    fun showRecipe(position: Int) {
        listener.showRecipe(recipes[position].first, Constants.SEARCH, user)
    }
}