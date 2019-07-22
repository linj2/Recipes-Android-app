package edu.rosehulman.menga.recipetracker

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText

class CommentAdapter(var context: Context) : RecyclerView.Adapter<CommentViewHolder>() {
    val comments = ArrayList<Comment>()

    init {
        //TODO: add snapshot Listener for the comment ref

    }

    override fun getItemCount() = comments.size

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): CommentViewHolder {
        Log.d(Constants.TAG,"Creating view holder for comment")
        val view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false)
        return CommentViewHolder(view,this,context)
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, index: Int) {
        viewHolder.bind(comments[index])
    }

    fun editCommentDialog(position: Int = -1){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.dialog_edit_title)

        val commentEditText:EditText = EditText(context)
        commentEditText.setText(comments[position].content)

        builder.setPositiveButton(android.R.string.ok){_, _ ->
            val content = commentEditText.text.toString()
            edit(position,content)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
    private fun remove(position: Int){
        //TODO: Firebase backend needs to delete
//        commentRef.document(comments[position].id).delete()
    }

    private  fun edit(position: Int,content:String){
        comments[position].content = content
        //TODO: Firebase backend needs to update too
//        commentRef.document(comments[position].id).set(commentss[position])
    }
}