package edu.rosehulman.menga.recipetracker

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Recipe(var title: String = "",
                  var ingredients: ArrayList<String> = ArrayList(),
                  var instructions: String = "", val uid: String = ""):Parcelable {

    @get:Exclude
    var id =""
    @ServerTimestamp
    var creation: Timestamp? = null

    companion object {
        const val CREATION_KEY = "creation"
        fun fromSnapshot(snapshot: DocumentSnapshot): Recipe {
            val pic = snapshot.toObject(Recipe::class.java)!!
            pic.id = snapshot.id
            return pic
        }
    }
}