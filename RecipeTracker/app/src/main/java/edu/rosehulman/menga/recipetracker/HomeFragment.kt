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

        val button_search = view.findViewById<Button>(R.id.button_search)
        val button_my_favorites = view.findViewById<Button>(R.id.button_my_favorites)
        val button_my_recipes = view.findViewById<Button>(R.id.button_my_recipes)
        val button_popular = view.findViewById<Button>(R.id.button_popular)

        button_search?.setOnClickListener {
            Log.d(Constants.TAG,"search clicked!!!")
            Snackbar.make(activity!!.findViewById(android.R.id.content),"search clicked",Snackbar.LENGTH_LONG).show()
        }

        //TODO: why we need long click listener for search button?
        button_search?.setOnLongClickListener {
            Toast.makeText(context, "search long clicked", Toast.LENGTH_SHORT).show()
            true
        }

        button_my_favorites?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"favorite clicked",Snackbar.LENGTH_SHORT).show()
        }
        button_my_recipes?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"my recipes clicked",Snackbar.LENGTH_SHORT).show()
        }
        button_popular?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"popular clicked",Snackbar.LENGTH_LONG).show()
        }

        return view
    }
}