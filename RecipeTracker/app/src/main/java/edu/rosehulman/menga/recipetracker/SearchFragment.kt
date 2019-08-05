package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.fragment_search.view.*
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


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
        searchButton.setOnClickListener {

            val query = searchBar.text.toString()
            //firebaseRecipeSearch(query)
            adapter = SearchAdapter(context!!,  listener!!, uid!!)

            //may need to research search algorithms
            view.recycler_view.setBackgroundColor(resources.getColor(R.color.colorBackground))
            view.recycler_view.adapter = adapter
            adapter.executeSearch(query)
            view.recycler_view.layoutManager = LinearLayoutManager(context)
            view.recycler_view.setHasFixedSize(true)

            //close the keyboard
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
        return view
    }
//    private fun firebaseRecipeSearch(query:String){
//        Toast.makeText(context, "Start Searching", Toast.LENGTH_LONG).show()
//
//        val firebaseSearchQuery = FirebaseDatabase.getInstance()
//            .reference
//            .child(Constants.RECIPES_PATH)
//            .child("title")
//            .startAt(query)
//            .endAt(query + "\uf8ff")
//
//        Log.d(Constants.TAG,"${firebaseSearchQuery.ref.key}")
//        val options = FirebaseRecyclerOptions.Builder<Recipe>()
//            .setQuery(firebaseSearchQuery,Recipe::class.java)
//            .setLifecycleOwner(this)
//            .build()
//        val firebaseRecyclerAdapter= object : FirebaseRecyclerAdapter<Recipe, RecipeViewHolder>(options) {
//            override fun onCreateViewHolder(parent: ViewGroup, index: Int): RecipeViewHolder {
//                return adapter.onCreateViewHolder(parent, index)
//            }
//
//            override fun onBindViewHolder(holder: RecipeViewHolder, position: Int, recipe: Recipe) {
//                holder.bind(recipe)
//            }
//        }
//        view?.recycler_view?.adapter = firebaseRecyclerAdapter
//
//    }
}