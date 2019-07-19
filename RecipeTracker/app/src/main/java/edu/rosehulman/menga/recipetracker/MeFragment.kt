package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import kotlinx.android.synthetic.main.fragment_me.view.*

class MeFragment: Fragment() {

    lateinit var adapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me,container,false)
        view.button_return.setOnClickListener {
            val switchTo = HomeFragment()
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        adapter = RecipeAdapter(context!!,  "")
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.setHasFixedSize(true)

        view.button_add_recipe.setOnClickListener {
            //TODO: get a new Recipe and add it in the adapter
            val builder = AlertDialog.Builder(context!!)
            // Set options
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) {_, _ ->
                val title = view.recipe_edit_title.text.toString()
                val ingredients = view.ingredients_edit_text.text.toString()
                val instructions = view.instructions_edit_text.text.toString()
                val ingredientList = toIngredientList(ingredients)
                val recipe = Recipe(title, ingredientList, instructions)
                adapter.add(recipe)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        }
        return view
    }

    //TODO: write method to turn a string into a list of strings using ', ' or some sort of delimiter
    fun toIngredientList(str: String): ArrayList<String> {
        var list = ArrayList<String>()
        list.add(str)
        return list
    }
}