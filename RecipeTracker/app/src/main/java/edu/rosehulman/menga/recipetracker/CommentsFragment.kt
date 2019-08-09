package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
const val ARG_UID = "uid"
class CommentsFragment: Fragment() {
    private var uid: String? =null

    companion object {
        fun newInstance(uid: String) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            uid = it?.getString(ARG_UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments,container,false)
        val commentButton = view.findViewById<Button>(R.id.comment_button)
        commentButton.setOnClickListener {

        }
        return view
    }
}