package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment: Fragment() {
    private var uid: String? = null
    private var listener: RecipeAdapter.OnRecipeSelectedListener? = null
    lateinit var adapter: SearchAdapter

    companion object {
        fun newInstance(uid: String) =
            SearchFragment().apply {
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
        val view = inflater.inflate(R.layout.fragment_search,container,false)
        view.button_return.setOnClickListener {
            val switchTo = HomeFragment.newInstance(uid!!)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        val searchButton = view.findViewById<Button>(R.id.button_execute_search)
        val searchBar = view.findViewById<EditText>(R.id.edit_text_search)
        view.button_execute_search.setOnClickListener {
            val query = searchBar.text.toString()
            adapter = SearchAdapter(context!!,  listener!!)

            //may need to research search algorithms
            view.recycler_view.setBackgroundColor(resources.getColor(R.color.colorBackground))
            view.recycler_view.adapter = adapter
            view.recycler_view.setHasFixedSize(true)
        }
        return view
    }
}