package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class HomeFragment:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)

        val buttonSearch = view.findViewById<Button>(R.id.button_search)
        val buttonMyFavorites = view.findViewById<Button>(R.id.button_my_favorites)
        val buttonMyRecipes = view.findViewById<Button>(R.id.button_my_recipes)
        val buttonPopular = view.findViewById<Button>(R.id.button_popular)

        //TODO: I was testing something and forgot to change it back.
        buttonSearch?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content), "search clicked", Snackbar.LENGTH_SHORT).show()
        }

        buttonMyFavorites?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"favorite clicked",Snackbar.LENGTH_SHORT).show()
        }
        buttonMyRecipes?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"my recipes clicked",Snackbar.LENGTH_SHORT).show()
        }
        buttonPopular?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"popular clicked",Snackbar.LENGTH_SHORT).show()
        }
        return view
    }
}