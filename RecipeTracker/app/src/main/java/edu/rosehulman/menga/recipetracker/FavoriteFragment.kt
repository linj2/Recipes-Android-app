package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favorite.view.*

class FavoriteFragment:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favorite,container,false)
        view.button_return.setOnClickListener {
            val switchTo = HomeFragment()
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo)
            for (i in 0 until activity!!.supportFragmentManager.backStackEntryCount) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        return view
    }
}