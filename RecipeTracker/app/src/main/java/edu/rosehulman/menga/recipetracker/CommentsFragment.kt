package edu.rosehulman.menga.recipetracker

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_edit_recipe.view.*
import android.view.Gravity
import android.widget.Toast



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
            recipeID = it.getString(Constants.ARG_RID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.comment_recycler_view)
        val adapter = CommentAdapter(context,(context as MainActivity).user!!,recipeID) //or just user
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter.update()

        val commentButton = view.findViewById<Button>(R.id.comment_button)
        val commentEditText = view.findViewById<EditText>(R.id.comment_EditText)
        commentButton.setOnClickListener {
            val content =commentEditText.text.toString()
            if(content.isEmpty()){
                val toast = Toast.makeText((context as MainActivity), "comment cannot be empty", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }else {
                val comment = Comment(content, user.uid, recipeID, user.displayName!!)
                adapter.add(comment)
                adapter.update()
                commentEditText.text.clear()
            }

            //close keyboard
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
        return view
    }
}