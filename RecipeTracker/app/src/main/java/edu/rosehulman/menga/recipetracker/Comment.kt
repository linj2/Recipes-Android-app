package edu.rosehulman.menga.recipetracker

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(var content: String?, val uid:String?, val recipeId:String?):Parcelable {
    @get:Exclude
    var id =""
    @ServerTimestamp
    var timeStamp: Timestamp? = null

    companion object {
        const val CREATION_KEY = "timeStamp"
        fun fromSnapshot(snapshot: DocumentSnapshot): Comment {
            val c = snapshot.toObject(Comment::class.java)!!
            c.id = snapshot.id
            return c
        }
    }
}