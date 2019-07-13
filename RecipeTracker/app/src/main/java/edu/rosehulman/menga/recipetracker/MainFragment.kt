package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main,container,false)
        button_search?.setOnClickListener {
            Snackbar.make(view, "search clicked", Snackbar.LENGTH_SHORT).show()
        }
        button_search?.setOnLongClickListener {
            Toast.makeText(context, "search clicked", Toast.LENGTH_SHORT).show()
            true
        }
        button_my_favorites?.setOnClickListener {
            Snackbar.make(view, "my_favorites clicked", Snackbar.LENGTH_SHORT).show()
        }
        button_my_recipes?.setOnClickListener {
            Snackbar.make(view, "my_recipes clicked", Snackbar.LENGTH_SHORT).show()
        }
        button_popular?.setOnClickListener {
            Snackbar.make(view, "popular clicked", Snackbar.LENGTH_SHORT).show()
        }
        return view
    }
}