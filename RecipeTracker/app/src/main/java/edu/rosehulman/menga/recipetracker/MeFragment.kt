package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import kotlinx.android.synthetic.main.fragment_me.view.*

class MeFragment: Fragment() {

    private val ARG_UID = "UID"
    private var uid: String? = null
    lateinit var adapter: RecipeAdapter
    private var listener: RecipeAdapter.OnRecipeSelectedListener? = null

    companion object {
        fun newInstance(uid: String) =
            MeFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_UID, uid)
                }
            }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is RecipeAdapter.OnRecipeSelectedListener) {
            listener = context
        }
        else {
            Log.e(Constants.TAG, "Should implement OnRecipeSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            uid = it?.getString(Constants.ARG_UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me,container,false)

        view.button_return.setOnClickListener {
            val switchTo = HomeFragment.newInstance(uid!!)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        adapter = RecipeAdapter(context!!, listener!!,  uid!!)
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.setHasFixedSize(true)
        adapter.update()

        view.button_add_recipe.setOnClickListener {
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
            var lastID = R.id.ingredients_edit_text
            editTextIds.add(lastID)
            dialog.setOnShowListener {
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                val layout = view.findViewById<RelativeLayout>(R.id.edit_recipe_layout)
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
                    val recipe = Recipe(title, ingredientList, instructions, uid!!)
                    adapter.add(recipe)
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
}