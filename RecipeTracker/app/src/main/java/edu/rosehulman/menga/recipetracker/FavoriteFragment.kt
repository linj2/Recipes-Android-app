package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favorite.view.*

class FavoriteFragment:Fragment() {
    private val ARG_UID: String = "UID"
    var uid: String? = null
    var listener: RecipeAdapter.OnRecipeSelectedListener? = null
    lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            uid = it?.getString(ARG_UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favorite,container,false)

        view.button_return.setOnClickListener {
            val switchTo = HomeFragment.newInstance(uid!!)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }

        adapter = FavoriteAdapter(context!!, listener!!, uid!!)
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.setHasFixedSize(true)
        return view
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

    companion object {
        fun newInstance(uid: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }
}