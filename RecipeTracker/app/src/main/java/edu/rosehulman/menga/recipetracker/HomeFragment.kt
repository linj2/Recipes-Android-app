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
import com.google.firebase.auth.FirebaseUser

class HomeFragment:Fragment() {
    private lateinit var user: FirebaseUser

    companion object {
        fun newInstance(user: FirebaseUser) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_USER, user)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            user = it?.getParcelable(Constants.ARG_USER)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)

//        val buttonSearch = view.findViewById<Button>(R.id.button_search)
//        val buttonMyFavorites = view.findViewById<Button>(R.id.button_my_favorites)
//        val buttonMyRecipes = view.findViewById<Button>(R.id.button_my_recipes)
//        val buttonPopular = view.findViewById<Button>(R.id.button_popular)

//        buttonSearch?.setOnClickListener {
//            Snackbar.make(activity!!.findViewById(android.R.id.content), "search clicked", Snackbar.LENGTH_SHORT).show()
//            val switchTo = SearchFragment.newInstance(uid!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, switchTo)
//            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
//                activity!!.supportFragmentManager.popBackStackImmediate()
//            }
//            ft.addToBackStack(Constants.SEARCH)
//            ft.commit()
//        }

//        buttonMyFavorites?.setOnClickListener {
//            Snackbar.make(activity!!.findViewById(android.R.id.content),"favorite clicked",Snackbar.LENGTH_SHORT).show()
//            val switchTo = FavoriteFragment.newInstance(uid!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, switchTo)
//            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
//                activity!!.supportFragmentManager.popBackStackImmediate()
//            }
//            ft.addToBackStack(Constants.FAVORITE)
//            ft.commit()
//        }
//        buttonMyRecipes?.setOnClickListener {
//            Snackbar.make(activity!!.findViewById(android.R.id.content),"my recipes clicked",Snackbar.LENGTH_SHORT).show()
//            val switchTo = MeFragment.newInstance(uid!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, switchTo)
//            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
//                activity!!.supportFragmentManager.popBackStackImmediate()
//            }
//            ft.addToBackStack(Constants.MY_RECIPES)
//            ft.commit()
//        }
//        buttonPopular?.setOnClickListener {
//            Snackbar.make(activity!!.findViewById(android.R.id.content),"popular clicked",Snackbar.LENGTH_SHORT).show()
//            val switchTo = PopularFragment.newInstance(uid!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, switchTo)
//            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
//                activity!!.supportFragmentManager.popBackStackImmediate()
//            }
//            ft.addToBackStack(Constants.POPULAR)
//            ft.commit()
//        }
        return view
    }
}