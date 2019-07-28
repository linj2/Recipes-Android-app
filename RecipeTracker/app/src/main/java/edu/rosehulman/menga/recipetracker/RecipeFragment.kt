package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
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
        view.button_return.setOnClickListener {
            when(previous) {
                Constants.MY_RECIPES -> {
                    val switchTo = MeFragment.newInstance(recipe!!.uid)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
//                    ft.addToBackStack(Constants.MY_RECIPES)
                    ft.commit()
                }
                Constants.POPULAR -> {
                    val switchTo = PopularFragment.newInstance(recipe!!.uid)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
                Constants.FAVORITE -> {
                    val switchTo = FavoriteFragment.newInstance(recipe!!.uid)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
                Constants.SEARCH -> {
                    val switchTo = SearchFragment.newInstance(recipe!!.uid)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, switchTo)
                    activity!!.supportFragmentManager.popBackStackImmediate()
                    ft.commit()
                }
            }
        }
        view.button_edit_recipe.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            val editTextIds = ArrayList<Int>()
            // Set options
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setNeutralButton("+", null)
            builder.setNegativeButton(android.R.string.cancel, null)
            val titleEditText = view.findViewById<EditText>(R.id.edit_title)
            val dialog = builder.create()
            val layout = view.findViewById<RelativeLayout>(R.id.edit_recipe_layout)
            var lastID = R.id.ingredients_edit_text
            var nextEditText = EditText(context)
            val instructionsText = view.findViewById<EditText>(R.id.instructions_edit_text)
            instructionsText.text.insert(0, recipe?.instructions)
            instructionsText.hint = context!!.resources.getString(R.string.instructions)
            for(ingredient in recipe?.ingredients ?: ArrayList()) {
                view.findViewById<EditText>(lastID).text.insert(0, ingredient)
                nextEditText = EditText(context)
                nextEditText.id = View.generateViewId()
                nextEditText.hint = context!!.resources.getString(R.string.ingredient)
                val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_END, lastID)
                layoutParams.addRule(RelativeLayout.ALIGN_START, lastID)
                layoutParams.addRule(RelativeLayout.BELOW, lastID)
                lastID = nextEditText.id
                nextEditText.layoutParams = layoutParams
                
                layout.addView(nextEditText)
                editTextIds.add(lastID)
            }
            layout.removeView(nextEditText)
            editTextIds.removeAt(editTextIds.size-1)
            lastID = editTextIds[editTextIds.size-1]

            dialog.setOnShowListener {
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                neutralButton.setOnClickListener {
                    val nextEditText = EditText(context)
                    nextEditText.id = View.generateViewId()
                    nextEditText.hint = context!!.resources.getString(R.string.ingredient)
                    val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.addRule(RelativeLayout.ALIGN_END, lastID)
                    layoutParams.addRule(RelativeLayout.ALIGN_START, lastID)
                    layoutParams.addRule(RelativeLayout.BELOW, lastID)
                    lastID = nextEditText.id
                    nextEditText.layoutParams = layoutParams
                    layout.addView(nextEditText)
                    editTextIds.add(lastID)
                }
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {dialogView: View ->
                    val title =titleEditText.text.toString()
                    val instructions = view.instructions_edit_text.text.toString()
                    val ingredientList = ArrayList<String>()
                    for(id in editTextIds) {
                        val ingredient = view.findViewById<EditText>(id).text.toString()
                        ingredientList.add(ingredient)
                    }
                    val r = Recipe(title, ingredientList, instructions, recipe!!.uid)
                    FirebaseFirestore.getInstance().collection(Constants.RECIPES_PATH)
                        .document(recipe!!.id).set(r)
                    it.dismiss()
                }
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setOnClickListener {dialogView: View ->
                    it.dismiss()
                }
            }
            dialog.show()
        }
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