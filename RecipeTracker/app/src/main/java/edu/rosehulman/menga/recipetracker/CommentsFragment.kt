package edu.rosehulman.menga.recipetracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*

const val ARG_UID = "uid"

class CommentsFragment : Fragment() {
    private lateinit var user: FirebaseUser
    private var recipeID:String? = null
    lateinit var adapter:CommentAdapter

    companion object {
        fun newInstance(user: FirebaseUser, recipeID:String) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.ARG_USER, user)
                    putString(Constants.ARG_RID,recipeID)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            user = it?.getParcelable(Constants.ARG_USER)!!
            recipeID = it?.getString(Constants.ARG_RID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.comment_recycler_view)
        val adapter = CommentAdapter(context,(context as MainActivity).user) //or just user
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val commentButton = view.findViewById<Button>(R.id.comment_button)
        commentButton.setOnClickListener {
            val content =view.findViewById<EditText>(R.id.comment_EditText).text.toString()
            val comment = Comment(content, user.uid, recipeID, user.displayName!!)
            adapter.add(comment)
        }
        return view
    }
}