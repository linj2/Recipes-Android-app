package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recipe_view.view.*

class RecipeFragment: Fragment() {
    var recipe: Recipe? = null
    var previous: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            recipe = it?.getParcelable(Constants.ARG_RECIPE)
            previous = it?.getString(Constants.ARG_PREVIOUS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recipe_view, container, false)
        view.recipe_view_title.text = recipe?.title
        return view
    }

    companion object {
        fun newInstance(recipe: Recipe, previousFragment: String) =
            RecipeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_RECIPE, recipe)
                    putString(Constants.ARG_PREVIOUS, previousFragment)
                }
            }
    }
}