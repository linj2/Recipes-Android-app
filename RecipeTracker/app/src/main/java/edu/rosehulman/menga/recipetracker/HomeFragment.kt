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
    private var uid: String? = null

    companion object {
        fun newInstance(uid: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_UID, uid)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            uid = it?.getString(Constants.ARG_UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)

        val buttonSearch = view.findViewById<Button>(R.id.button_search)
        val buttonMyFavorites = view.findViewById<Button>(R.id.button_my_favorites)
        val buttonMyRecipes = view.findViewById<Button>(R.id.button_my_recipes)
        val buttonPopular = view.findViewById<Button>(R.id.button_popular)

        //TODO: I was testing something and forgot to change it back.
        buttonSearch?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content), "search clicked", Snackbar.LENGTH_SHORT).show()
            val switchTo = SearchFragment()
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.addToBackStack(Constants.SEARCH)
            ft.commit()
        }

        buttonMyFavorites?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"favorite clicked",Snackbar.LENGTH_SHORT).show()
            val switchTo = FavoriteFragment.newInstance(uid!!)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.addToBackStack(Constants.FAVORITE)
            ft.commit()
        }
        buttonMyRecipes?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"my recipes clicked",Snackbar.LENGTH_SHORT).show()
            val switchTo = MeFragment.newInstance(uid!!)
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.addToBackStack(Constants.MY_RECIPES)
            ft.commit()
        }
        buttonPopular?.setOnClickListener {
            Snackbar.make(activity!!.findViewById(android.R.id.content),"popular clicked",Snackbar.LENGTH_SHORT).show()
            val switchTo = PopularFragment()
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.addToBackStack(Constants.POPULAR)
            ft.commit()
        }
        return view
    }
}