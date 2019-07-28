package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment: Fragment() {
    private var uid: String? = null

    companion object {
        fun newInstance(uid: String) =
            SearchFragment().apply {
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
        view.button_execute_search.setOnClickListener {
            val query = view.edit_text_search.text.toString()
            //TODO: execute search and add all of them to an adapter (not created yet)
            //may need to research search algorithms
            view.recycler_view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        }
        return view
    }
}