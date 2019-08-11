package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_comment.view.*
import kotlinx.android.synthetic.main.row_comment.view.comment

class CommentViewHolder: RecyclerView.ViewHolder{
    val usernameTextView: TextView = itemView.username
    val contentTextView: TextView = itemView.comment
    var context: Context

    constructor(itemView: View, adapter: CommentAdapter, context: Context):super(itemView){
        this.context = context
        itemView.setOnLongClickListener{
            Log.d(Constants.TAG,"selecting comment at $adapterPosition")
            if((context as MainActivity).uid == adapter.comments[adapterPosition].uid) {
                adapter.editCommentDialog(adapterPosition)
                adapter.notifyDataSetChanged()
            }else{
                adapter.authMessage()
            }
            true
        }
    }

    fun bind(comment: Comment) {
        Log.d(Constants.TAG,"Binding View holder for comment")
        usernameTextView.text = comment.userName
        contentTextView.text = comment.content
    }
}