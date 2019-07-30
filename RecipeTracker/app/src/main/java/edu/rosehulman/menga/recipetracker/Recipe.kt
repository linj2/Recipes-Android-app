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
                  var instructions: String = "", val uid: String = "",
                  var favoriteOf: String = ""): Parcelable {

    @get:Exclude
    var id =""
    @ServerTimestamp
    var creation: Timestamp? = null

    fun clone(): Recipe {
        return Recipe(title, ingredients, instructions, uid)
    }

    fun equals(other: Recipe): Boolean {
        return title==other.title && ingredients.equals(other.ingredients)
                && instructions == other.instructions && uid == other.uid
    }

    companion object {
        const val CREATION_KEY = "creation"
        fun fromSnapshot(snapshot: DocumentSnapshot): Recipe {
            val pic = snapshot.toObject(Recipe::class.java)!!
            pic.id = snapshot.id
            return pic
        }
    }
}