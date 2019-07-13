package edu.rosehulman.menga.recipetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_search.setOnClickListener {
            Snackbar.make(activity_main, "search clicked", Snackbar.LENGTH_SHORT)
        }
        button_search.setOnLongClickListener {
            Toast.makeText(this, "search clicked", Toast.LENGTH_SHORT)
            button_search.text = "recognized"
            true
        }
        button_my_favorites.setOnClickListener {
            Snackbar.make(activity_main, "my_favorites clicked", Snackbar.LENGTH_SHORT)
        }
        button_my_recipes.setOnClickListener {
            Snackbar.make(activity_main, "my_recipes clicked", Snackbar.LENGTH_SHORT)
        }
        button_popular.setOnClickListener {
            Snackbar.make(activity_main, "popular clicked", Snackbar.LENGTH_SHORT)
        }
    }
}
