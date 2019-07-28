package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_popular.view.*

class PopularFragment: Fragment() {
    private var uid: String? = null

    companion object {
        fun newInstance(uid: String) =
            PopularFragment().apply {
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
        val view = inflater.inflate(R.layout.fragment_popular,container,false)
        view.button_return.setOnClickListener {
            val switchTo = HomeFragment.newInstance(uid!!)
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