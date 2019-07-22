package edu.rosehulman.menga.recipetracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_comment.view.*

class CommentViewHolder: RecyclerView.ViewHolder{
    val usernameTextView: TextView = itemView.username
    val contentTextView: TextView = itemView.comment
    var context: Context

    constructor(itemView: View, adapter: CommentAdapter, context: Context):super(itemView){
        this.context = context
        itemView.setOnClickListener{
            Log.d(Constants.TAG,"selecting comment at $adapterPosition")
            //TODO: check id for permission of edit
//            if((context as MainActivity).uid == adapter.comments[adapterPosition].uid) {
                adapter.editCommentDialog(adapterPosition)
//            }else{
//                adapter.authMessage()
//            }
        }
    }

    fun bind(comment: Comment) {
        Log.d(Constants.TAG,"Binding View holder for comment")
        usernameTextView.text = comment.user
        contentTextView.text = comment.content
    }
}